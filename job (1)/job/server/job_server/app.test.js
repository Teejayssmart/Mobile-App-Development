const path = require('path');
const serverPath = path.resolve(__dirname, './server');  // Adjust this to match the relative path to your server.js
console.log(serverPath);  // Logs the absolute path to server.js



const request = require('supertest');
const app = require(serverPath);  // Adjust this path to match your project structure

describe('User API tests', () => {
  it('should register a user successfully', async () => {
    const response = await request(app)
      .post('/user/register')
      .send({
        account: 'user1',
        password: 'password123',
        balance: '100.00'
      });

    expect(response.status).toBe(200);
    expect(response.body.msg).toBe('register successfully');
  });

  it('should login a user successfully', async () => {
    const response = await request(app)
      .post('/user/login')
      .send({
        account: 'user1',
        password: 'password123'
      });

    expect(response.status).toBe(200);
    expect(response.body.data.u_account).toBe('user1');
    expect(response.body.data.u_balance).toBe('100.00');
  });
});
