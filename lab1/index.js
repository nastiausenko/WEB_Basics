const express = require('express');
const session = require('express-session');
const sharedSession = require('express-socket.io-session');
const path = require('path');

const app = express();
const http = require('http').Server(app);
const io = require('socket.io')(http);

app.use(express.static(path.join(__dirname, '/public')));
app.use(express.urlencoded({ extended: true }));

// сесія
const sessionMiddleware = session({
  secret: 'supersecretkey',
  resave: false,
  saveUninitialized: false,
  cookie: { maxAge: 1000 * 60 * 60 }
});
app.use(sessionMiddleware);

io.use(sharedSession(sessionMiddleware, { autoSave: true }));

app.get('/login', (req, res) => {
  res.sendFile(path.join(__dirname, 'public/login.html'));
});

app.post('/login', (req, res) => {
  const { username, room } = req.body;
  if (!username || !room) {
    return res.send(' Треба ввести ім’я і кімнату');
  }

  if (rooms[room] && rooms[room].users.includes(username)) {
    return res.send(' Це ім’я вже зайняте у цій кімнаті. Вибери інше.');
  }

  req.session.username = username;
  req.session.room = room;
  return res.redirect('/');
});

app.get('/logout', (req, res) => {
  req.session.destroy(() => {
    res.redirect('/login');
  });
});

app.get('/', (req, res) => {
  if (!req.session.username || !req.session.room) {
    return res.redirect('/login'); 
  }
  res.sendFile(path.join(__dirname, 'index.html'));
});

const rooms = {}; 

io.on('connection', (socket) => {
   const sess = socket.handshake.session;
  if (!sess.username || !sess.room) {
    socket.emit('forceLogout');
    return socket.disconnect();
  }

  const username = sess.username;
  const room = sess.room;

  if (rooms[room] && rooms[room].users.includes(username)) {
    socket.emit('forceLogout'); 
    return socket.disconnect(true);
  }

  socket.join(room);
  if (!rooms[room]) {
    rooms[room] = { users: [], messages: [] };
  }
  rooms[room].users.push(username);

  socket.emit('chat history', rooms[room].messages);

  io.to(room).emit('roomData', { room, users: rooms[room].users });

  socket.emit('chat message', ` Привіт, ${username}! Ти у кімнаті: ${room}`);

  if (isFirstJoin) {
    socket.broadcast.to(room).emit('chat message', ` ${username} приєднався`);
  }

  socket.on('chat message', (msg) => {
    const fullMsg = `${username}: ${msg}`;
    rooms[room].messages.push(fullMsg);
    io.to(room).emit('chat message', fullMsg);
  });

  socket.on('disconnect', () => {
    const socketsInRoom = Array.from(io.sockets.adapter.rooms.get(room) || []);
    const stillConnected = socketsInRoom.some(
      id => io.sockets.sockets.get(id)?.handshake.session.username === username
    );

    if (!stillConnected) {
      rooms[room].users = rooms[room].users.filter(u => u !== username);
      io.to(room).emit('chat message', `${username} покинув кімнату`);
      io.to(room).emit('roomData', { room, users: rooms[room].users });
    }
  });

  
});


http.listen(3000, () => console.log('listening on *:3000'));
