const rooms = {}; 

module.exports = {
  rooms,

  addUser(username, room) {
    if (!rooms[room]) rooms[room] = { users: [], messages: [] };
    if (!rooms[room].users.includes(username)) {
      rooms[room].users.push(username);
      return true;
    }
    return false; 
  },

  removeUser(username, room) {
    if (rooms[room]) {
      rooms[room].users = rooms[room].users.filter(u => u !== username);
      if (rooms[room].users.length === 0) delete rooms[room];
    }
  },

  addMessage(room, message) {
    if (!rooms[room]) rooms[room] = { users: [], messages: [] };
    rooms[room].messages.push(message);
  },

  getUsers(room) {
    return rooms[room] ? rooms[room].users : [];
  },

  getMessages(room) {
    return rooms[room] ? rooms[room].messages : [];
  },

  isNameTaken(username, room) {
    return rooms[room] && rooms[room].users.includes(username);
  }
};
