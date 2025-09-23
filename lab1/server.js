const express = require('express');
const session = require('express-session');
const sharedSession = require('express-socket.io-session');
const path = require('path');
const chatManager = require('./chatManager');

const app = express();
const http = require('http').Server(app);
const io = require('socket.io')(http);

app.use(express.urlencoded({ extended: true }));


const sessionMiddleware = session({
    secret: 'supersecretkey',
    resave: false,
    saveUninitialized: false,
    cookie: { maxAge: 1000 * 60 * 60 }
});
app.use(sessionMiddleware);
io.use(sharedSession(sessionMiddleware, { autoSave: true }));

require('./routes')(app);
app.use(express.static(path.join(__dirname, 'public')));

io.on('connection', (socket) => {
    const sess = socket.handshake.session;
    if (!sess.username || !sess.room) {
        socket.emit('forceLogout');
        return socket.disconnect();
    }

    const { username, room } = sess;

    const isFirstJoin = chatManager.addUserSocket(username, room, socket.id);

    socket.join(room);

    socket.emit('chat history', chatManager.getMessages(room));
    io.to(room).emit('roomData', { room, users: chatManager.getUsers(room) });


    if (isFirstJoin) {
        socket.emit('chat message', { text: `Привіт, ${username}!`, type: 'system' });
        socket.broadcast.to(room).emit('chat message', { text: `${username} приєднався`, type: 'system' });
    }

    socket.on('chat message', (msg) => {
        const fullMsg = `${username}: ${msg}`;
        chatManager.addMessage(room, fullMsg);
        io.to(room).emit('chat message', { text: fullMsg, type: 'user' });
    });

    socket.on('disconnect', () => {
        chatManager.removeUserSocket(username, room, socket.id);

        if (!socket.handshake.session.username) {
            chatManager.forceRemoveUser(username, room);
            io.to(room).emit('chat message', { text: `${username} покинув кімнату`, type: 'system' });
            io.to(room).emit('roomData', { room, users: chatManager.getUsers(room) });
        }
    });

});

http.listen(3000, () => console.log('Server running on http://localhost:3000'));
