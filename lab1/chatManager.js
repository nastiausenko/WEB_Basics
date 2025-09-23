const rooms = {};

module.exports = {
    rooms,

    addUserSocket(username, room, socketId) {
        if (!rooms[room]) rooms[room] = { users: [], messages: [], userSockets: {} };

        const isNewUser = !rooms[room].users.includes(username);

        if (isNewUser) {
            rooms[room].users.push(username);
            rooms[room].userSockets[username] = [];
        }

        rooms[room].userSockets[username].push(socketId);

        return isNewUser;
    },


    removeUserSocket(username, room, socketId) {
        if (!rooms[room] || !rooms[room].userSockets[username]) return false;

        rooms[room].userSockets[username] = rooms[room].userSockets[username].filter(id => id !== socketId);

        return rooms[room].userSockets[username].length > 0;
    },

    forceRemoveUser(username, room) {
        if (!rooms[room]) return;

        delete rooms[room].userSockets[username];
        rooms[room].users = rooms[room].users.filter(u => u !== username);

        if (rooms[room].users.length === 0) {
            delete rooms[room];
        }
    },

    addMessage(room, message) {
        if (!rooms[room]) rooms[room] = { users: [], messages: [], userSockets: {} };
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
