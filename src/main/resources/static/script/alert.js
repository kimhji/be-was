async function formResponseProcessToMain(response) {
    await formResponseProcess(response, "./index.html")
}
async function formResponseProcessToLogin(response) {
    await formResponseProcess(response, "./login/index.html")
}
async function formResponseProcessToRegistration(response) {
    await formResponseProcess(response, "./index.html")
}

async function formResponseProcess(response, nextPath) {
    if(await alertCall(response)) return;

    // 성공
    if (response.status >= 200 && response.status < 300 && nextPath != null && nextPath.length > 0) {
        window.location.href = nextPath;
    }
}

async function getView(response) {
    if(await alertCall(response)) return;


    const html = await response.text();
    document.open();
    document.write(html);
    document.close();
}

async function alertCall(response){
    if (response.status >= 400 && response.status < 500) {
        const msg = await response.text();
        alert("에러 발생: " + msg);
        return true;
    }
    return false;
}

function addListenerAddComment(){
    const btn = document.getElementById('create_comment_btn');

    btn?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const postId = btn.dataset.postId;
        if (!postId) return;
        const content = document.querySelector("#content").value;

        const response = await fetch(`/comment/${postId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                content
            })
        });

        await formResponseProcess(response, `/post/${postId}`);
    });
}

document.getElementById("login")?.addEventListener("submit", async (e) => {
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
    if(response.status >= 400 && response.status < 500){
        const text = await response.text();
        if (text === '사용자 데이터가 존재하지 않습니다.') {
            const goSignup = confirm(
                '존재하지 않는 아이디입니다.\n회원 가입 하시겠습니까?'
            );

            if (goSignup) {
                location.href = '/registration/index.html';
            }
            return;
        }
        alert("에러 발생: " + text);
        return;
    }

    await formResponseProcessToMain(response);
});

document.getElementById("registration")?.addEventListener("submit", async (e) => {
    e.preventDefault(); // 기본 submit 막기
    const userId = document.querySelector("#userId").value;
    const password = document.querySelector("#password").value;
    const name = document.querySelector("#name").value;

    const response = await fetch("/user/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            userId,
            password,
            name
        })
    });

    await formResponseProcessToLogin(response);
});

document.getElementById("user_update")?.addEventListener("submit", async (e) => {
    e.preventDefault(); // 기본 submit 막기
    const userName = document.querySelector("#update-name").value;
    const password = document.querySelector("#update-password").value;
    const checkPassword = document.querySelector("#update-check-password").value;

    const response = await fetch("/user/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            userName,
            password,
            checkPassword
        })
    });

    await formResponseProcessToMain(response);
});

document.getElementById("link_to_mypage")?.addEventListener("click", async (e) => {
    e.preventDefault(); // 기본 submit 막기

    const response = await fetch("/mypage", {
        method: "GET"
    });

    await getView(response);
});

document.getElementById("logout-btn")?.addEventListener("click", async (e) => {
    e.preventDefault(); // 기본 submit 막기

    const response = await fetch("/user/logout", {
        method: "POST"
    });

    await formResponseProcessToMain(response);
});

document.getElementById("post")?.addEventListener("submit", async (e) => {
    e.preventDefault(); // 기본 submit 막기

    const form = e.target;
    const formData = new FormData(form);

    const response = await fetch("/post/create", {
        method: "POST",
        body: formData
    });

    await formResponseProcessToMain(response);
});

addListenerAddComment();