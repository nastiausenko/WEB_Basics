const path = require('path');
const chatManager = require('./chatManager');

module.exports = app => {
    const io = app.get('io');

    app.get('/login', (req, res) => res.sendFile(path.join(__dirname, '/public/login.html')));

    app.post('/login', (req, res) => {
        const { username, room } = req.body;
        if (!username || !room) return res.json({ success: false, message: 'Введіть ім’я і кімнату' });
        if (chatManager.isNameTaken(username, room)) return res.json({ success: false, message: 'Це ім’я вже зайняте у цій кімнаті. Виберіть інше.' });

        req.session.username = username;
        req.session.room = room;
        res.json({ success: true });
    });

    app.post('/logout', (req, res) => {
        const { username, room } = req.session;
        if (username && room) {
            chatManager.forceRemoveUser(username, room);
            io.to(room).emit('roomData', { room, users: chatManager.getUsers(room) });
        }
        req.session.destroy(() => res.redirect('/login'));
    });

    app.get('/', (req, res) => {
        if (!req.session.username || !req.session.room) return res.redirect('/login');
        res.sendFile(path.join(__dirname, 'public/index.html'));
    });
};