
const mysql = require('mysql2/promise');
const express = require('express');
const cors = require('cors')

const app = express();
app.use(express.urlencoded({ extended: true }));
app.use(cors());
app.use(express.json());

const pool = mysql.createPool({
    host: "localhost",
    user: "root",
    password: "a123",
    database: "job",
    waitForConnections: true,
    connectionLimit: 20,
    queueLimit: 0
});

//register
app.post('/user/register', async (req, resp) => {
    let { account, password, balance } = req.body;

    const sql = "insert into user_table values (?,?,?)";
    try {
        await pool.execute(sql, [account, password, balance]);
        resp.send('{"status":200,"msg":"register successfully"}');
    } catch (error) {
        resp.status(500).json({ status: 500, msg: "try later" });
    }

})

//login, response user information if login successfully
app.post('/user/login', async (req, resp) => {
    let { account, password } = req.body;
    let sql = "select * from user_table where u_account=? and u_password=?";

    try {
        const [rows] = await pool.execute(sql, [account, password]);
        if (rows.length == 0) {
            resp.send('{"status":500,"msg":"account or password wrong"}');
            return;
        }
        resp.send('{"status":200,"data":' + JSON.stringify(rows[0]) + '}');
    } catch (error) {
        resp.status(500).json({ status: 500, msg: "try later" });
    }
})

//post new job.(set time as the current time,and state as 0, which means it's not taken yet )
app.post('/job/post', async (req, resp) => {
    const { user, title, content, price } = req.body;
    let sql = "insert into job_table values (null,?,?,?,?,0,'',null,now())";

    try {
        await pool.execute(sql, [user, title, content, price]);
        resp.send('{"status":200,"msg":"post successfully"}');
    } catch (error) {
        resp.status(500).json({ status: 500, msg: "try later" });
    }
})

//view job by key word(searching function),or account1(for what job a user post), or account2(for what jobs a user applied), or state
app.get("/job/view", async (req, resp) => {
    let { key, account1, account2, state } = req.query;
    let sql = "select * from job_table  where 1=1";

    if (key && key != '') {
        sql += " and (j_title like '%" + key + "%'  or j_content like '%" + key + "%')";
    }
    if (account1 && account1 != '') {
        sql += " and j_user = '" + account1 + "'";
    }
    if (account2 && account2 != '') {
        sql += " and j_apply_list like '%" + account2 + "%'";
    }
    if (state && state != '') {
        sql += " and j_state=" + state;
    }
    sql += " order by j_time";

    try {
        const [rows] = await pool.execute(sql);
        resp.send('{"status":200,"data":' + JSON.stringify(rows) + '}');
    } catch (error) {
        resp.status(500).json({ status: 500, msg: "try later" });
    }

})

//apply job, add a user account into apply list
app.post('/job/apply', async (req, resp) => {
    let { account, no } = req.body;
    let sql = "update job_table set  j_apply_list = concat(j_apply_list,'" + account + " ') where j_no=" + no;
    try {
        await pool.execute(sql);
        resp.send('{"status":200,"data":"apply successfully"}');
    } catch (error) {
        resp.status(500).json({ status: 500, msg: "try later" });
    }
})
//aprove job, poster approve someone to take job, which changes state as 1 and take money away from poster temporary
app.post('/job/approve', async (req, resp) => {
    let { no, account1, account2, money } = req.body;
    let sql1 = "update job_table set j_state=1, j_taken = ? where j_no=" + no;
    let sql2 = "update user_table set u_balance=u_balance-" + money + " where u_account=?";

    const conn = await pool.getConnection(); 
    try {
        await conn.beginTransaction(); 
        await conn.execute(sql1, [account2]);
        await conn.execute(sql2, [account1]);
        await conn.commit(); 
        resp.send('{"status":200,"data":"approve successfully"}');
    } catch (error) {
        await conn.rollback(); 
        resp.status(500).json({ status: 500, msg: "try later" });
    }
})

//finish job, user finish the job he did, change state as 2
app.post('/job/finish', async (req, resp) => {
    let { no } = req.body;

    let sql = "update job_table set j_state=2 where j_no=" + no;
    try {
        await pool.execute(sql);
        resp.send('{"status":200,"data":"finish successfully"}');
    } catch (error) {
        resp.status(500).json({ status: 500, msg: "try later" });
    }
})

//pay job, 1.change job state as 3(done)  2. add money on the user who did the job
app.post('/job/pay', async (req, resp) => {
    let { no, account, money } = req.body;

    let sql1 = "update job_table set j_state=3 where j_no=" + no;
    let sql2 = "update user_table set u_balance=u_balance+" + money + " where u_account=?";

    const conn = await pool.getConnection(); 
    try {
        await conn.beginTransaction(); 
        await conn.execute(sql1);
        await conn.execute(sql2, [account]);
        await conn.commit(); 
        resp.send('{"status":200,"data":"pay successfully"}');
    } catch (error) {
        await conn.rollback(); 
        resp.status(500).json({ status: 500, msg: "try later" });
    }
})

app.listen(3000, () => {
    console.log('server started');
})