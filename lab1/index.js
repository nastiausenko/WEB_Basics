const express = require('express');
const app = express();
const http = require('http').Server(app);
const io = require('socket.io')(http);

app.get('/', (req, res) => {
  res.sendFile(__dirname + '/index.html');
});

io.on('connection', (socket) => {
  console.log('a user connected');

  socket.on('joinRoom', ({ username, room }) => {
    socket.username = username;
    socket.room = room;
    socket.join(room);

    socket.emit('chat message', `Привіт, ${username}! Ти у кімнаті: ${room}`);

    socket.broadcast.to(room).emit('chat message', ` ${username} приєднався до кімнати`);
  });

  socket.on('chat message', (msg) => {
    if (socket.room) {
      io.to(socket.room).emit('chat message', `${socket.username}: ${msg}`);
    }
  });

  socket.on('disconnect', () => {
    if (socket.room) {
      io.to(socket.room).emit('chat message', ` ${socket.username} покинув кімнату`);
    }
  });
});

http.listen(3000, () => {
  console.log('listening on *:3000');
});
