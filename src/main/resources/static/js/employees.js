async function loadEmployees() {
  const res = await fetch(`${API_BASE}/employees/me`, { headers: authHeaders() });
  if (!res.ok) return; // handle 401/err
  const e = await res.json();
  document.getElementById("empTable").innerHTML = `
    <tr><td>${e.username||""}</td><td>${e.fullName||""}</td><td>${e.role||""}</td></tr>`;
}
loadEmployees();