var mainimg = document.getElementById("mainimg")
var max;
var filedir
var currentP= 1
var chapters;
var readerChapter;
var fragmentElement = document.getElementById("NewComment").children[1].cloneNode(true);
fragmentElement.style.width = "100%";
var child = fragmentElement.children[1];
child.removeChild(child.children[0]);
child.innerHTML += "<button type='button' class='btn btn-primary'>Update!</button>";
child.children[0].classList.add("update-btn");
fragmentElement.children[1].innerHTML += "<button type='button' class='btn btn-dark'>Close</button>";
child.children[1].classList.add("update-btn");


 function getchapter(pages){
    var init ={
        method: 'GET'
    }
    fetch('http://localhost:8080/NitroReader/Chapter?option=getchapter&currentChap='+localStorage.currentChap+"&mangaid="+localStorage.mangaid,init)
    .then(function(res){
        return res.json()
    }).then(function(resa){
        var res = resa.data
        document.getElementById("manga_name").textContent = res.manganame
        document.getElementById("chapter_title").textContent = localStorage.currentChap+ " - " + res.chaptertitle
        
        max = res.max;
        if(max>0){
            switch(localStorage.estadoReader){
                case "up":
                currentP=1
                filedir= res.filedir;
                 mainimg.setAttribute("src", filedir+"/"+ currentP+".png");
                break;
                case "down":
                currentP = max
                filedir=    res.filedir;
                mainimg.setAttribute("src", filedir+"/"+ currentP+".png");
                break;
                case "tracker":
                let chapid = getChapter_id();
                currentP = pages["p"+chapid];
                filedir= res.filedir;
                mainimg.setAttribute("src", filedir+"/"+ currentP+".png");
                break;
            }
            if (localStorage.user != null){startLikesC();}
        }else{
            alert("este capitulo se encuentra vacio!")
            chapterfinished(localStorage.mangaid, getChapter_id(), currentP)
        }
        
        
    })
    
    
}




document.addEventListener("keydown", function(e){
    switch(e.keyCode){
        case 37:
            if(currentP > 1 ){
                currentP--
                mainimg.setAttribute("src", filedir+"/"+ currentP+".png");
            }else{
                ordenChap()
                if(chapters["nombre"+(readerChapter-1)]){
                    localStorage.setItem("estadoReader", "down") 
                    alert("redirigiendo al capitulo anterior")
                    localStorage.setItem("currentChap", chapters["nombre"+(readerChapter-1)])
                     window.location.href ="Reader.html"
                }
            }
            break;
        case 39:
            if(currentP < max){
                currentP++
                mainimg.setAttribute("src", filedir+"/"+ currentP+".png");
            }else{
                ordenChap()
                if(chapters["nombre"+(readerChapter+1)]){
                    localStorage.setItem("estadoReader", "up") 
                    alert("capitulo finalizado, ha sido redirigido al siguiente capitulo")
                    chapterfinished(localStorage.mangaid, getChapter_id(), currentP)
                    localStorage.setItem("currentChap", chapters["nombre"+(readerChapter+1)])
                     window.location.href ="Reader.html"
                }else{
                    alert("capitulo finalizado, ha llegado al final del manga.")
                    mangafinished(localStorage.mangaid, true)
                    chapterfinished(localStorage.mangaid, getChapter_id(), currentP)
                }
                
            }
            break;
    }
})


function loadnumchapter(){
    var init ={
        method: 'GET'
    }
    fetch('http://localhost:8080/NitroReader/Chapter?option=getnumchapters&mangaid='+localStorage.mangaid,init)
    .then(function(res){
        return res.json()
    }).then(function(resa){
        var res = resa.data
        var count =0
        for (key in res){
            if(key.indexOf("nombre")!= -1){
                count++
            }
        }
        chapters = res;
        for(let i=0; i<count ; i++){
            let a = document.createElement("a");
            a.setAttribute("id", res["id"+i])
            a.setAttribute("class", "dropdown-item")
            a.setAttribute("href", "#")
            a.textContent = res["nombre"+i];
            a.addEventListener("click",function(){
                localStorage.setItem("currentChap", res["nombre"+i])
                localStorage.setItem("estadoReader", "tracker")
                window.location.href ="Reader.html"
                 })
            document.getElementById("dropmenu").appendChild(a);
        }
        
    }).then(()=> startReader())
    
    
}

ordenChap = function(){
    var count =0
        for (key in chapters){
            if(key.indexOf("nombre")!= -1){
                count++
            }
        }
    for(let i=0; i<count; i++){
        if(localStorage.currentChap == chapters["nombre"+i]){
            readerChapter = i;
        }
    }
}

function mangafinished(manga_id, finished){
        let data = {manga_id: manga_id, finished: finished   }
        
    let init = {method:'POST', body:JSON.stringify(data), headers:{'Content-Type': 'application/json'}}

    fetch("http://localhost:8080/NitroReader/TrackerMangaServl",init)
    .then(res => res.json()).then((res) => {console.log(res.data.message)
    })
    }

function chapterfinished(manga_id , chapter_id, page_tracker){

    let data = {manga_id: manga_id,
                chapter_id: chapter_id,
                page_tracker: page_tracker ,
                finished: true                  
                                        }
    let init = {method:'POST', body:JSON.stringify(data), headers:{'Content-Type': 'application/json'}}

fetch("http://localhost:8080/NitroReader/TrackerChapterServl",init)
.then(res => res.json()).then((res) => {console.log(res.data.message)
})
    }

  function updateChapterTracker(manga_id, chapter_id, page_tracker){
    let data = {manga_id: manga_id,
        chapter_id: chapter_id,
        page_tracker: page_tracker ,
        finished: false                  
                                }
let init = {method:'POST', body:JSON.stringify(data), headers:{'Content-Type': 'application/json'}}

fetch("http://localhost:8080/NitroReader/TrackerChapterServl",init)
.then(res => res.json()).then((res) => {console.log(res.data.message)
})
  }


  document.getElementById("saveTracker").addEventListener("click",function(){
    updateChapterTracker(localStorage.mangaid, getChapter_id() , currentP)
  }) 


  function getChapter_id(){
   let tags = document.getElementById("dropmenu").getElementsByTagName("a")
   for(let i = 0; i<tags.length; i++){
       if(localStorage.currentChap == tags[i].textContent){
           return tags[i].getAttribute("id")
       }
   }
  }


  function startReader(){
    
    let tags = document.getElementById("dropmenu").getElementsByTagName("a")
    let count  = tags.length;
    let data={"numchaps":count, "manga_id": parseInt(localStorage.mangaid)  }
    for(let i = 0; i<count; i++){
        data["id"+i] = parseInt(tags[i].getAttribute("id"));
    }
    let init = {method:'POST', body:JSON.stringify(data), headers:{'Content-Type': 'application/json'}}
    console.log(init.body)
    fetch("http://localhost:8080/NitroReader/TrackerChapterGETServl",init)
    .then(res => res.json()).then((resa) => {
        var res = resa.data
        for(let i=0; i<count; i++){
           
            let id = tags[i].getAttribute("id")
            console.log(res)
            console.log(res[id])
            if(res[id] == true){
                document.getElementById(id).style.backgroundColor = "lightcoral"
            }
        }
        getchapter(res);
    })
  }

  document.getElementById("gomangainfo").addEventListener("click", ()=> window.location.href ="http://localhost:8080/NitroReader/MangaInfo.html?manga="+localStorage.mangaid)

  function showComments(){
      var chap_id = getChapter_id();
      localStorage.setItem("chapter_id", chap_id)
    fetch(`http://localhost:8080/NitroReader/CommentChapterServ?chapter_id=${getChapter_id()}`, {method: 'GET'})
        .then(res => res.json()).then((resa) => {
        if (resa.status === 200) {
            var res = resa.data
            console.log(res)
            document.getElementById("NewComment").children[0].textContent = localStorage.getItem("user");
            if (res.data.logged){ //If is loggedd put the buttons of delete and edit on the comment
                document.getElementById("sndComment").addEventListener("click", sendComment);
                if (res.data.hasOwnProperty("comments")){
                    res.data.comments.forEach((element) => {
                        if (element.owned) {
                            getComments(element.name, element.comment, true);
                        }else{
                            getComments(element.name, element.comment, false);
                        }
                    })
                }
            }else{
                document.getElementById("sndComment").setAttribute("data-toggle", "modal");
                document.getElementById("sndComment").setAttribute("data-target", "#notLogged");
                if (res.data.hasOwnProperty("comments")){
                    res.data.comments.forEach((element) => {
                        getComments(element.name, element.comment, false);
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
function getComments(name, comment, condition){
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
        let s2 = document.createElement("span");
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
function newComment(name, comment) {
    let c = document.createElement("div");
    c.classList.add("comment");
    let n = document.createElement("div");
    n.classList.add("name");
    n.textContent = name;
    c.appendChild(n);
    let e = document.createElement("div");
    let s = document.createElement("span");
    s.addEventListener("click", editComment);
    let s2 = document.createElement("span");
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
    let commentBox = document.getElementById("Comments");
    commentBox.insertBefore(c, commentBox.childNodes[0]);
}
function sendComment() {
    document.getElementById("sndComment").removeEventListener("click", sendComment);
    let commentContent = document.getElementById("comment-text").value;
    console.log(commentContent);
    let chapterid = getChapter_id();
    let data = {user_id: localStorage.getItem("user_id"),
        chapter_id: chapterid,
        newComment: commentContent,
    };
    fetch("http://localhost:8080/NitroReader/CommentChapterServ", {method:'POST', body:JSON.stringify(data), headers: {'Content-Type': 'application/json'}})
        .then(res => res.json()).then((resa) => {
            var res = resa.data
            console.log(res)
            console.log(data)
        if (res.status === 201) {
            newComment(res.data.user_name, res.data.comment);
            document.getElementById("sndComment").addEventListener("click", sendComment);
        }
    }).catch((error) => {
        console.log(error);
        
    })

}
function editComment() {
    let p1 = this.parentNode;
    let p2 = p1.parentNode;
    let comment = p2.children[2];
    comment.classList.add("hidde");
    a = fragmentElement.cloneNode(true);
    a.children[0].value = comment.textContent;
    a.children[1].children[0].addEventListener("click", updateComment);
    a.children[1].children[1].addEventListener("click", removeUpdate);
    p2.appendChild(a);
    this.removeEventListener("click", editComment);
}
function removeUpdate() {
    let p1 = this.parentNode;
    let p2 = p1.parentNode;
    let p3 = p2.parentNode;
    p3.children[1].children[0].addEventListener("click", editComment);
    p3.children[2].classList.remove("hidde");
    p3.removeChild(p2);
}
function updateComment() {
    let p1 = this.parentNode;
    let p2 = p1.parentNode;
    let p3 = p2.parentNode;
    let newC = p2.children[0].value;
    let data = {newComment: newC,
        user_id:localStorage.getItem("user_id"),
        chapter_id: localStorage.chapter_id,
        comment: p3.children[2].textContent};

    fetch("http://localhost:8080/NitroReader/CommentChapterServ",{method:'PUT', body:JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
        .then(res => res.json()).then((resa) => {
            var res = resa.data
        if (res.status === 200) {
            p3.children[2].textContent = newC;
            p3.children[2].classList.remove("hidde");
            p3.children[1].children[0].addEventListener("click", editComment);
            p3.removeChild(p2);
        }
    }).catch((error) => {
        console.log(error);
    })
}
function deleteComment() {
    let p1 = this.parentNode;
    let p2 = p1.parentNode;
    let p3 = p2.parentNode;
    data = {user_id: localStorage.getItem("user_id"), chapter_id: localStorage.chapter_id, comment: p2.children[2].textContent};
    fetch("http://localhost:8080/NitroReader/CommentChapterServ", {method:'DELETE', body:JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
        .then(res => res.json()).then((resa) => {
            var res = resa.data
        if (res.status === 200) {
            p3.removeChild(p2);
        }
    }).catch((error) => {
        console.log(error);
    })

}




//FUNCTION TO MAKE A REQUEST THAT LIKES THE CHAPTER
function likeManga() {
    var chapter_id = getChapter_id();
    this.removeEventListener("click", likeManga);
    data = {chapter_id: chapter_id, switchState : "ON"}
    fetch("http://localhost:8080/NitroReader/LikeChapterServ", {method:'POST', body: JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
        .then(res => res.json()).then((res) =>{
            if (res.status === 200){
                document.getElementById("likeC").setAttribute("data-original-title", `${res.data.likesChapter}`);
                if (res.data.like){
                    document.getElementById("likeC").style.backgroundColor = "green";
                }
                document.getElementById("likeC").addEventListener("click", removeLike);
            }
    }).catch((error) =>{
        console.log(error);
    })
}

//FUNCTION TO REMOVE THE LIKE FROM THE CHAPTER
function removeLike() {
    var chapter_id = getChapter_id();
    this.removeEventListener("click", removeLike);
    data = {chapter_id: chapter_id, switchState : "OFF"}
    fetch("http://localhost:8080/NitroReader/LikeChapterServ", {method:'POST', body: JSON.stringify(data), headers:{'Content-Type': 'application/json'}})
        .then(res => res.json()).then((res) =>{
        if (res.status === 200){
            document.getElementById("likeC").setAttribute("data-original-title", `${document.getElementById("likeC").getAttribute("data-original-title") - "1"}`);
            if (!res.data.like){
                document.getElementById("likeC").style.backgroundColor = "black";
            }
            document.getElementById("likeC").addEventListener("click", likeManga);
        }
    }).catch((error) =>{
        console.log(error);
    })
}


function startLikesC(){
    var chapter_id = getChapter_id();
fetch(`http://localhost:8080/NitroReader/LikeChapterServ?Chapter_id=${chapter_id}`, {method:'GET'})
.then(res => res.json()).then((res) =>{
    document.getElementById("likeC").setAttribute("data-original-title", `${res.data.likesChapter}`);
            if (localStorage.user != null){
                if (res.data.like){
                    document.getElementById("likeC").style.backgroundColor = "green";
                    document.getElementById("likeC").addEventListener("click", removeLike);
                } else{
                    document.getElementById("likeC").addEventListener("click", likeManga);
                } } else{
                    document.getElementById("likeC").setAttribute("data-toggle", "modal");
                    document.getElementById("likeC").setAttribute("data-target", "#notLogged");
                }
            
    
 })
  }

  document.getElementById("likeC").addEventListener("click", ()=>{
    if (localStorage.user == null){
        document.getElementById("likeC").setAttribute("data-toggle", "modal");
        document.getElementById("likeC").setAttribute("data-target", "#notLogged");
    }
  })
