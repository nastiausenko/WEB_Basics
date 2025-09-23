const path = require('path');
const chatManager = require('./chatManager');

module.exports = (app) => {

    app.get('/login', (req, res) => {
        res.sendFile(path.join(__dirname, 'public/login.html'));
    });

    app.post('/login', (req, res) => {
        const { username, room } = req.body;

        if (!username || !room) {
            return res.send('Треба ввести ім’я і кімнату');
        }

        if (chatManager.isNameTaken(username, room)) {
            return res.send('Це ім’я вже зайняте у цій кімнаті. Вибери інше.');
        }

        req.session.username = username;
        req.session.room = room;
        res.redirect('/');
    });

    app.get('/logout', (req, res) => {
        const { username, room } = req.session;
        req.session.destroy(() => {
            chatManager.forceRemoveUser(username, room);
            res.redirect('/login');
        });
    });


    app.get('/', (req, res) => {
        if (!req.session.username || !req.session.room) {
            return res.redirect('/login');
        }
        res.sendFile(path.join(__dirname, 'public/index.html'));
    });

};
