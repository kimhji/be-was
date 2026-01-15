function addListenerLikeAction(){
    const btn = document.getElementById('post__like__btn');
    if(btn == null) return;
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

function addListenerSaveTempImage(){
    const imageInput = document.getElementById('form_profile_image');
    if(imageInput == null) return;

    imageInput.addEventListener('change', async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = () => {
            document.getElementById('user-profile-preview').src = reader.result;
        };
        reader.readAsDataURL(file);
    });
}


addListenerLikeAction();
addListenerSaveTempImage();