async function loadDashboard() {
  const res = await fetch(API_BASE + "/api/manager/reports/summary", { headers: authHeaders() });
  if (!res.ok) return;
  const data = await res.json();
  document.getElementById("revenue").innerText = data.revenue + " đ";
  document.getElementById("stock").innerText = data.stock;
  renderChart(data.labels, data.values);
}

function renderChart(labels, values) {
  const ctx = document.getElementById("revenueChart").getContext("2d");
  new Chart(ctx, {
    type: "line",
    data: { labels, datasets: [{ label: "Doanh thu", data: values, borderColor: "#2563eb" }] },
    options: { scales: { y: { beginAtZero: true } } }
  });
}

loadDashboard();
