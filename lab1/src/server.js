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

app.set('io', io);

require('./routes')(app);
app.use(express.static(path.join(__dirname, '/public')));

io.on('connection', (socket) => {
    const sess = socket.handshake.session;
    if (!sess.username || !sess.room) {
        socket.emit('forceLogout');
        return socket.disconnect();
    }

    const { username, room } = sess;

    socket.emit('currentUser', username);

    const isFirstJoin = chatManager.addUserSocket(username, room, socket.id);

    socket.join(room);

    socket.emit('chat history', chatManager.getMessages(room));
    io.to(room).emit('roomData', { room, users: chatManager.getUsers(room) });


    if (isFirstJoin) {
        const welcomeMsg = { text: `Привіт, ${username}!`, type: 'system' };
        const joinMsg = { text: `${username} приєднався`, type: 'system' };

        chatManager.addMessage(room, joinMsg);

        socket.emit('chat message', welcomeMsg);
        socket.broadcast.to(room).emit('chat message', joinMsg);
    }


    socket.on('chat message', (msg) => {
        const fullMsg = { text: `${username}: ${msg}`, type: 'user' };
        chatManager.addMessage(room, fullMsg);
        io.to(room).emit('chat message', fullMsg);
    });

    socket.on('disconnect', () => {
        setTimeout(() => {
            const stillConnected = chatManager.removeUserSocket(username, room, socket.id);

            if (!stillConnected) {
                chatManager.forceRemoveUser(username, room);

                const leaveMsg = { text: `${username} покинув кімнату`, type: 'system' };
                chatManager.addMessage(room, leaveMsg);

                io.to(room).emit('chat message', leaveMsg);
                io.to(room).emit('roomData', { room, users: chatManager.getUsers(room) });
            }
        }, 1000); 
    });
});

http.listen(3000, () => console.log('Server running on http://localhost:3000'));
