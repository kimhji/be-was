function addListenerLikeAction(){
    const btn = document.getElementById('post__like__btn');

    btn.addEventListener('click', async () => {
        const postId = btn.dataset.postId;
        if (!postId) return;

        const response = await fetch(`/post/like/${postId}`, {
            method: "POST"
        });

        if (!response.ok) return;

        const data = await response.json();

        document.querySelector('.post__like__count').textContent = data.likes;
    });

}

addListenerLikeAction();