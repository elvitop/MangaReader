// General Stuff

//FUNCTION TO GET THE PARAMETERS AT THE URL
var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
        }
    }
};

var fragmentElement = document.getElementById("NewComment").children[1].cloneNode(true);
fragmentElement.style.width = "100%";
var child = fragmentElement.children[1];
child.removeChild(child.children[0]);
child.innerHTML += "<button type='button' class='btn btn-primary'>Update!</button>";
child.children[0].classList.add("update-btn");
fragmentElement.children[1].innerHTML += "<button type='button' class='btn btn-dark'>Close</button>";
child.children[1].classList.add("update-btn");


//////////////////////////////////////////////////////////////////////////

function load() {
    fetch(`http://localhost:8080/NitroReader/CRUDMangaServlet?manga=${getUrlParameter("manga")}`, {method:'GET'}).then(res => res.json())
    .then(res => {
        document.getElementById("mangaImage").src = `http://localhost:8080/NitroReader/library/${res.data.location}`;
        document.getElementById("mangaName").textContent = res.data.manga_name;
        document.getElementById("mangaSynopsis").textContent = res.data.manga_synopsis;
        if (res.status === 200) {
            localStorage.setItem("manga_status", res.data.manga_status);
            if (res.data.manga_status) {
                document.getElementById("mangaStatuss").innerHTML = "<i class='fas fa-check-circle'></i> Ongoing";
            } else{
                document.getElementById("mangaStatuss").innerHTML = "<i class='fas fa-ban'></i> Finished";
            }
            res.data.genres.forEach(element => {
                let li = document.createElement("li");
                li.setAttribute("class", "genre");
                li.textContent = element;
                document.getElementById("mangaGenres").appendChild(li);
            });
            document.getElementById("likeM").setAttribute("data-original-title", `${res.data.likesManga}`);
            if (res.data.owner) {
                let hidde = document.getElementsByClassName('hidde');
                while(hidde.length > 0) {
                    hidde[0].classList.remove('hidde');
                }
            }
            if (res.data.logged){
                if (res.data.like){
                    document.getElementById("likeM").style.backgroundColor = "green";
                    document.getElementById("likeM").addEventListener("click", removeLike);
                } else{
                    document.getElementById("likeM").addEventListener("click", likeManga);
                }
                if (res.data.subscribe){
                    document.getElementById("subscribe").style.backgroundColor = "green";
                    document.getElementById("subscribe").addEventListener("click", removeSubscription);
                }else{
                    document.getElementById("subscribe").addEventListener("click", subscribeManga);
                }
            } else{
                document.getElementById("likeM").setAttribute("data-toggle", "modal");
                document.getElementById("likeM").setAttribute("data-target", "#notLogged");
                document.getElementById("subscribe").setAttribute("data-toggle", "modal");
                document.getElementById("subscribe").setAttribute("data-target", "#notLogged");
            }


        }

    })
}

//FUNCTION TO FETCH AL THE COMMENTS OF THE MANGA
function showComments(){
    let data = {manga_id : getUrlParameter("manga")}
    fetch(`http://localhost:8080/NitroReader/CommentManga?manga_id=${getUrlParameter("manga")}`, {method: 'GET'})
    .then(res => res.json()).then((res) => {
        if (res.status === 200) {
            document.getElementById("NewComment").children[0].textContent = localStorage.getItem("user");
            if (res.data.logged){ //If is loggedd put the buttons of delete and edit on the comment
                document.getElementById("sndComment").addEventListener("click", sendComment);
                if (res.data.hasOwnProperty("comments")){
                    res.data.comments.forEach((element) => {
                        if (element.owned) {
                            getComments(element.name, element.comment, element.comment_id, true);
                        }else{
                            getComments(element.name, element.comment, -1, false);
                        }
                    })
                }
            }else{
                document.getElementById("sndComment").setAttribute("data-toggle", "modal");
                document.getElementById("sndComment").setAttribute("data-target", "#notLogged");
                if (res.data.hasOwnProperty("comments")){
                    res.data.comments.forEach((element) => {
                        getComments(element.name, element.comment, -1, false);
                    })
                }
            }
            document.getElementById("titleComments").classList.remove("hidde");
            document.getElementById("Comments").classList.remove("hidde");
            document.getElementById("NewComment").classList.remove("hidde");
            this.classList.add("hidde");
        }
    }).catch((error) => {console.log(error);})
    
}
document.getElementById("btnComments").addEventListener("click", showComments);

//FUNCTION TO APPEND ALL THE COMMENTS THAT ARE FETCHED FROM THE SERVER
function getComments(name, comment, id, condition){
    if (condition) {
    let c = document.createElement("div");
    c.classList.add("comment");
    let n = document.createElement("div");
    n.classList.add("name");
    n.textContent = name;
    c.appendChild(n);
    let e = document.createElement("div");
    let s = document.createElement("span");
    s.addEventListener("click", editComment);
    s.id = id;
    let s2 = document.createElement("span");
    s2.id = id;
    s2.addEventListener("click", deleteComment);
    let b = document.createElement("button");
    b.classList.add("btn");
    b.classList.add("btn-link");
    let b2 = document.createElement("button");
    b2.classList.add("btn");
    b2.classList.add("btn-danger");
    let i = document.createElement("i");
    let i2= document.createElement("i");
    i.classList.add("fas");
    i.classList.add("fa-pencil-alt");
    i2.classList.add("fas");
    i2.classList.add("fa-times");
    b.appendChild(i);
    b.appendChild(document.createTextNode(" Edit"));
    b2.appendChild(i2);
    s.appendChild(b);
    s2.appendChild(b2);
    e.classList.add("editions");
    e.appendChild(s);
    e.appendChild(s2);
    c.appendChild(e);
    let p = document.createElement("p");
    p.classList.add("comment-content");
    p.textContent = comment;
    c.appendChild(p);
    document.getElementById("Comments").appendChild(c);
    } else{
    let c = document.createElement("div");
    c.classList.add("comment");
    let n = document.createElement("div");
    n.classList.add("name");
    n.textContent = name;
    c.appendChild(n);
    let p = document.createElement("p");
    p.classList.add("comment-content");
    p.textContent = comment;
    c.appendChild(p);
    document.getElementById("Comments").appendChild(c);
    }
    
}

//FUNCTION TO APPEND A NEW COMMENT CREATED
function newComment(name, comment, id) {
    let c = document.createElement("div");
    c.classList.add("comment");
    let n = document.createElement("div");
    n.classList.add("name");
    n.textContent = name;
    c.appendChild(n);
    let e = document.createElement("div");
    let s = document.createElement("span");
    s.addEventListener("click", editComment);
    s.id = id;
    let s2 = document.createElement("span");
    s2.addEventListener("click", deleteComment);
    s2.id = id;
    let b = document.createElement("button");
    b.classList.add("btn");
    b.classList.add("btn-link");
    let b2 = document.createElement("button");
    b2.classList.add("btn");
    b2.classList.add("btn-danger");
    let i = document.createElement("i");
    let i2= document.createElement("i");
    i.classList.add("fas");
    i.classList.add("fa-pencil-alt");
    i2.classList.add("fas");
    i2.classList.add("fa-times");
    b.appendChild(i);
    b.appendChild(document.createTextNode(" Edit"));
    b2.appendChild(i2);
    s.appendChild(b);
    s2.appendChild(b2);
    e.classList.add("editions");
    e.appendChild(s);
    e.appendChild(s2);
    c.appendChild(e);
    let p = document.createElement("p");
    p.classList.add("comment-content");
    p.textContent = comment;
    c.appendChild(p);
    let commentBox = document.getElementById("Comments");
    commentBox.insertBefore(c, commentBox.childNodes[0]);
}

//FUNCTION TO MAKE A REQUEST TO CREATE A COMMENT
function sendComment() {
    document.getElementById("sndComment").removeEventListener("click", sendComment);
    let commentContent = document.getElementById("comment-text").value;
    console.log(commentContent);
    let manga = getUrlParameter("manga");
    let data = {user_id: localStorage.getItem("user_id"),
            manga_id: manga,
            newComment: commentContent,
            }
    fetch("http://localhost:8080/NitroReader/CommentManga", {method:'POST', body:JSON.stringify(data), headers: {'Content-Type': 'application/json'}})
    .then(res => res.json()).then((res) => {
        if (res.status == 200) {
            newComment(res.data.user_name, res.data.comment, res.data.comment_id);

            document.getElementById("sndComment").addEventListener("click", sendComment);
        }
    }).catch((error) => {
        console.log(error);        
    })

}


//FUNCTION TO SET A TEXTAREA TO THE COMMENT TO BE ABLE TO EDIT THE COMMENT
function editComment() {
    let p1 = this.parentNode;
    let p2 = p1.parentNode;
    let comment = p2.children[2];    
    comment.classList.add("hidde");
    a = fragmentElement.cloneNode(true);
    a.children[0].value = comment.textContent;
    a.children[1].children[0].addEventListener("click", updateComment);
    a.children[1].children[0].id = this.id;
    a.children[1].children[1].addEventListener("click", removeUpdate);
    p2.appendChild(a);
    this.removeEventListener("click", editComment);
}

//FUNCTION TO REMOVE THE UPDATE AREA FROM THE WHITHIN THE COMMENT
function removeUpdate() {
    let p1 = this.parentNode;
    let p2 = p1.parentNode;
    let p3 = p2.parentNode;
    p3.children[1].children[0].addEventListener("click", editComment);
    p3.children[2].classList.remove("hidde");
    p3.removeChild(p2);
}

//FUNCTION THE MAKE THE REQUEST FOR UPDATE THE COMMENT
function updateComment() {
    let p1 = this.parentNode;
    let p2 = p1.parentNode;
    let p3 = p2.parentNode;
    let newC = p2.children[0].value;
    let data = {newComment: newC,
                comment_id: this.id}
    fetch("http://localhost:8080/NitroReader/CommentManga",{method:'PUT', body:JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
    .then(res => res.json()).then((res) => {
        if (res.status == 200) {
            p3.children[2].textContent = newC;
            p3.children[2].classList.remove("hidde");
            p3.children[1].children[0].addEventListener("click", editComment);
            p3.removeChild(p2);
        }
    }).catch((error) => {
        console.log(error);
    })
}

//FUNCTION TO MAKE A REQUEST THAT DELETE THE COMMENT
function deleteComment() {
    let p1 = this.parentNode;
    let p2 = p1.parentNode;
    let p3 = p2.parentNode;
    data = {manga_id: getUrlParameter("manga"), comment: p2.children[2].textContent, comment_id: this.id}
    fetch("http://localhost:8080/NitroReader/CommentManga", {method:'DELETE', body:JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
    .then(res => res.json()).then((res) => {
        if (res.status === 200) {
            p3.removeChild(p2);
        }
    }).catch((error) => {
        console.log(error);
    })

}

//FUNCTION TO MAKE A REQUEST THAT LIKES THE MANGA
function likeManga() {
    this.removeEventListener("click", likeManga);
    data = {manga_id: getUrlParameter("manga"), user_id:localStorage.getItem("user_id"), switchState : "ON"}
    fetch("http://localhost:8080/NitroReader/LikeManga", {method:'POST', body: JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
        .then(res => res.json()).then((res) =>{
            if (res.status === 200){
                document.getElementById("likeM").setAttribute("data-original-title", `${res.data.likesManga}`);
                if (res.data.like){
                    document.getElementById("likeM").style.backgroundColor = "green";
                }
                document.getElementById("likeM").addEventListener("click", removeLike);
            }
    }).catch((error) =>{
        console.log(error);
    })
}

//FUNCTION TO REMOVE THE LIKE FROM THE MANGA
function removeLike() {
    this.removeEventListener("click", removeLike);
    data = {manga_id: getUrlParameter("manga"), user_id:localStorage.getItem("user_id"), switchState : "OFF"}
    fetch("http://localhost:8080/NitroReader/LikeManga", {method:'POST', body: JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
        .then(res => res.json()).then((res) =>{
        if (res.status === 200){
            document.getElementById("likeM").setAttribute("data-original-title", `${document.getElementById("likeM").getAttribute("data-original-title") - "1"}`);
            if (!res.data.like){
                document.getElementById("likeM").style.backgroundColor = "black";
            }
            document.getElementById("likeM").addEventListener("click", likeManga);
        }
    }).catch((error) =>{
        console.log(error);
    })
}

function subscribeManga() {
    this.removeEventListener("click", subscribeManga);
    data = {manga_id: getUrlParameter("manga"), user_id:localStorage.getItem("user_id")}
    fetch("http://localhost:8080/NitroReader/Subscription", {method:'POST', body: JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
        .then(res => res.json()).then((res) =>{
        if (res.status === 201){
            if (res.data.subscribe){
                document.getElementById("subscribe").style.backgroundColor = "green";
            }
            document.getElementById("subscribe").addEventListener("click", removeSubscription);
        }
    }).catch((error) =>{
        console.log(error);
    })
}

function removeSubscription() {
    this.removeEventListener("click", removeSubscription);
    data = {manga_id: getUrlParameter("manga"), user_id:localStorage.getItem("user_id")}
    fetch("http://localhost:8080/NitroReader/Subscription", {method:'DELETE', body: JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
        .then(res => res.json()).then((res) =>{
        if (res.status === 200){
            if (!res.data.subscription){
                document.getElementById("subscribe").style.backgroundColor = "black";
            }
            document.getElementById("subscribe").addEventListener("click", subscribeManga);
        }
    }).catch((error) =>{
        console.log(error);
    })
}

function deleteManga() {
    let data = {
        manga_id: getUrlParameter('manga_id')
    }
    fetch('/NitroReader/CRUDMangaServlet', {method:'DELETE', headers:{'Content-Type': 'application/json'}, body: JSON.stringify(data)})
    .then(res => res.json()).then(res => {
        if (res.status === 200) {
            window.location.href = '/NitroReader/index.html';
        }
    })
}
document.getElementById('delete').addEventListener('click', deleteManga);