let stompClient = null;
let notifCount = 0;

function initWebsocket() {
  const socket = new SockJS(`${API_BASE}/ws`);
  stompClient = Stomp.over(socket);
  stompClient.connect({}, frame => {
    stompClient.subscribe("/topic/promotions", msg => notify("Khuyến mãi mới!"));
    stompClient.subscribe("/topic/reports", msg => notify("Báo cáo cập nhật!"));
  });
}

function notify(text) {
  notifCount++;
  const div = document.createElement("div");
  div.className = "alert alert-info position-fixed end-0 bottom-0 m-3";
  div.innerHTML = text;
  document.body.appendChild(div);
  setTimeout(() => div.remove(), 3000);
}

initWebsocket();
