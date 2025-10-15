document.getElementById("btnLoadReport").onclick = async () => {
  const from = document.getElementById("fromDate").value;
  const to = document.getElementById("toDate").value;
  if (!from || !to) return alert("Chọn khoảng thời gian");
  const res = await fetch(`${API_BASE}/reports/revenue?from=${from}&to=${to}`, { headers: authHeaders() });
  const data = await res.json();
  renderReportChart(data.map(d => d.date), data.map(d => d.revenue));
};

const btnExport = document.getElementById("btnExportCsv");
if (btnExport) {
  btnExport.onclick = async () => {
    const from = document.getElementById("fromDate").value;
    const to = document.getElementById("toDate").value;
    if (!from || !to) return alert("Chọn khoảng thời gian");
    const res = await fetch(`${API_BASE}/reports/revenue.csv?from=${from}&to=${to}`, { headers: authHeaders() });
    const blob = await res.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `revenue_${from}_${to}.csv`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  };
}

function renderReportChart(labels, values) {
  const ctx = document.getElementById("reportChart").getContext("2d");
  new Chart(ctx, {
    type: "bar",
    data: {
      labels,
      datasets: [{ label: "Doanh thu", data: values, backgroundColor: "#2563eb" }]
    },
    options: { scales: { y: { beginAtZero: true } } }
  });
}
