// ✅ URL backend
const API_BASE = "http://localhost:8080/api";

// 🔹 Hàm đăng nhập
async function login(username, password) {
  try {
    const res = await fetch(API_BASE + "/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password })
    });

    // Nếu backend trả lỗi
    if (!res.ok) {
      const text = await res.text();
      console.error("❌ Backend error:", res.status, text);
      throw new Error(text || `Lỗi ${res.status}`);
    }

    // Thử parse JSON
    const data = await res.json();
    console.log(data);
    if (!data.token) {
      throw new Error("Phản hồi không hợp lệ từ server");
    }

    console.log("✅ Đăng nhập thành công:", data);
    localStorage.setItem("token", data.token);
    localStorage.setItem("role", data.role);
    if (data.userId) localStorage.setItem("userId", data.userId);
    return data;
  } catch (err) {
    console.error("⚠️ Lỗi trong login:", err);
    throw err;
  }
}
