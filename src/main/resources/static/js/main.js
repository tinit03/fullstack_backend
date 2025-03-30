'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let nickname = null;
let fullname = null;
let selectedUserId = null;

function connect(event) {
  nickname = document.querySelector('#nickname').value.trim();
  fullname = document.querySelector('#fullname').value.trim();

  console.log("checking that mail and item has content")
  if (nickname && fullname) {
    usernamePage.classList.add('hidden');
    chatPage.classList.remove('hidden');

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    console.log("Connecting...")
    stompClient.connect({}, onConnected, onError);
  }
  event.preventDefault();
}

// todo: Connect to ws at the start of the application of the login page
function onConnected() {
  stompClient.subscribe(`/user/${nickname}/queue/messages/${fullname}`, onMessageReceived);

  console.log("Connected")
  document.querySelector('#connected-user-fullname').textContent = nickname;
  findAndDisplayConnectedUsers().then();
}

async function findAndDisplayConnectedUsers() {
  console.log("Fetching users")

  const connectedUsersResponse = await fetch('/users');

  console.log("Fetched users")

  let connectedUsers = await connectedUsersResponse.json();

  console.log(connectedUsers)

  connectedUsers = connectedUsers.filter(user => user.email !== nickname);
  const connectedUsersList = document.getElementById('connectedUsers');
  connectedUsersList.innerHTML = '';

  connectedUsers.forEach(user => {
    appendUserElement(user, connectedUsersList);
    if (connectedUsers.indexOf(user) < connectedUsers.length - 1) {
      const separator = document.createElement('li');
      separator.classList.add('separator');
      connectedUsersList.appendChild(separator);
    }
  });
}

function appendUserElement(user, connectedUsersList) {
  const listItem = document.createElement('li');
  listItem.classList.add('user-item');
  listItem.id = user.email;

  const userImage = document.createElement('img');
  userImage.alt = ":)";

  const usernameSpan = document.createElement('span');
  console.log("setting text content", use.email)
  usernameSpan.textContent = user.email;

  const receivedMsgs = document.createElement('span');
  receivedMsgs.textContent = '0';
  receivedMsgs.classList.add('nbr-msg', 'hidden');

  listItem.appendChild(userImage);
  listItem.appendChild(usernameSpan);
  listItem.appendChild(receivedMsgs);

  listItem.addEventListener('click', userItemClick);

  connectedUsersList.appendChild(listItem);
}

function userItemClick(event) {
  document.querySelectorAll('.user-item').forEach(item => {
    item.classList.remove('active');
  });
  messageForm.classList.remove('hidden');

  const clickedUser = event.currentTarget;
  clickedUser.classList.add('active');

  console.log(clickedUser)

  selectedUserId = clickedUser.getAttribute('id');
  console.log(selectedUserId)
  fetchAndDisplayUserChat().then();

  const nbrMsg = clickedUser.querySelector('.nbr-msg');
  nbrMsg.classList.add('hidden');
  nbrMsg.textContent = '0';

}

function displayMessage(senderId, content) {
  const messageContainer = document.createElement('div');
  messageContainer.classList.add('message');
  if (senderId === nickname) {
    messageContainer.classList.add('sender');
  } else {
    messageContainer.classList.add('receiver');
  }
  const message = document.createElement('p');
  message.textContent = content;
  messageContainer.appendChild(message);
  chatArea.appendChild(messageContainer);
}

async function fetchAndDisplayUserChat() {
  const userChatResponse = await fetch(`/messages/${fullname}/${nickname}/${selectedUserId}`);
  const userChat = await userChatResponse.json();
  console.log("userchat", userChat)
  chatArea.innerHTML = '';
  userChat.forEach(chat => {
    displayMessage(chat.senderId, chat.content);
  });
  chatArea.scrollTop = chatArea.scrollHeight;
}


function onError() {
  connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
  connectingElement.style.color = 'red';
}


function sendMessage(event) {
  const messageContent = messageInput.value.trim();
  console.log("selectedUser", selectedUserId)
  if (messageContent && stompClient) {
    const chatMessage = {
      senderId: nickname,
      recipientId: selectedUserId,
      itemId: fullname,
      content: messageInput.value.trim(),
      timestamp: new Date()
    };
    console.log("Sending chat", chatMessage)
    stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
    displayMessage(nickname, messageInput.value.trim());
    messageInput.value = '';
  }
  chatArea.scrollTop = chatArea.scrollHeight;
  event.preventDefault();
}


async function onMessageReceived(payload) {
  await findAndDisplayConnectedUsers();
  console.log('Message received', payload);
  const message = JSON.parse(payload.body);
  if (selectedUserId && selectedUserId === message.senderId) {
    displayMessage(message.senderId, message.content);
    chatArea.scrollTop = chatArea.scrollHeight;
  }

  if (selectedUserId) {
    document.querySelector(`#${selectedUserId}`).classList.add('active');
  } else {
    messageForm.classList.add('hidden');
  }

  const notifiedUser = document.querySelector(`#${message.senderId}`);
  if (notifiedUser && !notifiedUser.classList.contains('active')) {
    const nbrMsg = notifiedUser.querySelector('.nbr-msg');
    nbrMsg.classList.remove('hidden');
    nbrMsg.textContent = '';
  }
}

function onLogout() {
  stompClient.send("/app/user.disconnectUser",
      {},
      JSON.stringify({nickName: nickname, fullName: fullname, status: 'OFFLINE'})
  );
  window.location.reload();
}

usernameForm.addEventListener('submit', connect, true); // step 1
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.onbeforeunload = () => onLogout();