
document.getElementById("login").addEventListener("submit", async (e) => {
        e.preventDefault(); // 기본 submit 막기
        const userId = document.querySelector("#userId").value;
        const password = document.querySelector("#password").value;

        const response = await fetch("/user/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            userId,
            password
        })
    });

    if (response.status >= 400 && response.status < 500) {
        const msg = await response.text();
        alert("에러 발생: " + msg);
        return;
    }
    if(response.status >= 300 && response < 400){
        window.location.href = response.headers.get("Location");
        return;
    }

    // 성공
    if(response.status >= 200 && response.status<300) {
        window.location.href = "/index.html";
    }
});