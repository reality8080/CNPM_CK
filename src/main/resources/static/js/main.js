const API_BASE = "http://localhost:8080/api/manager"; // fix CORS

async function loadPage(page) {
  const res = await fetch(`pages/${page}.html`);
  const html = await res.text();
  document.getElementById("content").innerHTML = html;

  const oldScript = document.getElementById("pageScript");
  if (oldScript) oldScript.remove();

  const script = document.createElement("script");
  script.src = `js/${page}.js?v=${Date.now()}`;
  script.id = "pageScript";
  document.body.appendChild(script);
}

document.querySelectorAll(".nav-link").forEach(a => {
  a.addEventListener("click", e => {
    e.preventDefault();
    document.querySelectorAll(".nav-link").forEach(x => x.classList.remove("active"));
    a.classList.add("active");
    loadPage(a.dataset.page);
  });
});

document.getElementById("btnLogout").onclick = () => {
  localStorage.clear();
  location.reload();
};

// load trang mặc định
loadPage("dashboard");
