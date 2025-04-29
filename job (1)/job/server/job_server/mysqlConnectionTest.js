 const mysql = require('mysql2/promise'); // Uncomment this line if you're using mysql connection

// Create a connection pool to interact with the database
const pool = mysql.createPool({
  host: 'localhost',
  user: 'root',
  password: 'Wordiskids1@',
  database: 'mobile_app',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

// Function to register a new user in the database
async function registerUser(account, password, balance) {
  const sql = "INSERT INTO user_table (u_account, u_password, u_balance) VALUES (?, ?, ?)";
  try {
    // Execute the query using the provided parameters
    const [result] = await pool.execute(sql, [account, password, balance]);
    return { status: 200, msg: 'User registered successfully' };
  } catch (error) {
    console.error('Error registering user:', error);
    return { status: 500, msg: 'Registration failed' };
  }
}

// Function to login a user
async function loginUser(account, password) {
  const sql = "SELECT * FROM user_table WHERE u_account = ? AND u_password = ?";
  try {
    // Execute the query to get the user data
    const [rows] = await pool.execute(sql, [account, password]);

    // If no user found, return an error
    if (rows.length === 0) {
      return { status: 500, msg: 'Invalid credentials' };
    }

    // Return the user data if found
    return { status: 200, data: rows[0] };
  } catch (error) {
    console.error('Error logging in:', error);
    return { status: 500, msg: 'Login failed' };
  }
}

// Export the functions to be used in the routes or test files
module.exports = { registerUser, loginUser };
