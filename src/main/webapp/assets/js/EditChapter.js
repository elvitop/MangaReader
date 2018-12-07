var loginbtn = document.getElementById("loginbtn")
var registerbtn= document.getElementById("registerbtn")
var logoutbtn = document.getElementById("logoutbtn")
var div1 = document.getElementById("div1")
//los eventos para el redireccionado
registerbtn.addEventListener("click", function(){window.location.href ="NitroRegister.html"})
document.getElementById("homebtn").addEventListener("click", function(){window.location.href ="index.html"})
document.getElementById("gohome").addEventListener("click", function(){window.location.href ="index.html"})
loginbtn.addEventListener("click", function(){window.location.href ="NitroLogin.html"})

function main(){
    if(localStorage.user){
        var userspan= document.getElementById("userspan");
        userspan.textContent = localStorage.user;
        loginbtn.style.display = "none";
        registerbtn.style.display= "none"
    }else{
        logoutbtn.style.display = "none"
        
    }
}

logoutbtn.addEventListener("click", function(){
    

    fetch('http://localhost:8080/NitroReader/Session', {
    method: 'GET', 
    withCredentials: true,
    credentials: 'same-origin',
    headers:{
        'Content-Type': 'application/json'
    }
    }).then(res => res.json()).then(function(){
       localStorage.clear();
       window.location.replace("index.html");
    })
    .catch(error => console.error('Error:', error))
})



var div2 = document.getElementById("div2")

function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
    ev.preventDefault();
    var data = ev.dataTransfer.getData("text");
    if(data[0] != "z")
    ev.target.appendChild(document.getElementById(data));
}

function dropcajas(ev){
    ev.preventDefault();
    var data = ev.dataTransfer.getData("text");
    if(data[0] != "z" && ev.target.childNodes.length < 1){
    ev.target.appendChild(document.getElementById(data));
    }
}


function dropdiv2(ev){
    var data = ev.dataTransfer.getData("text");
    if(ev.target.id == "div2" && data[0] != "z"){
    ev.preventDefault;
    var d= document.createElement("div");
    var n = (div2.childNodes.length)+1;
    d.setAttribute("id", "z"+ n)
    d.setAttribute("class","border border-danger sisisi")
    d.setAttribute("ondrop", "dropcajas(event)")
    d.setAttribute("ondragover","allowDrop(event)")
    d.setAttribute("draggable","true")
    d.setAttribute("ondragstart", "drag(event)");
    d.appendChild(document.getElementById(data))
    div2.appendChild(d)
    }
}

function dropdel(e){
    e.preventDefault();
    var el = document.getElementById(e.dataTransfer.getData('Text'));
    el.parentNode.removeChild(el);
}

//aqui empieza el mani con los files
document.getElementById("cargar").addEventListener("click", pullfiles);

function pullfiles(){
    var selectedFiles = document.getElementById('myfiles').files
    
    for (let i = 0; i < selectedFiles.length; i++){ 
        var img = document.createElement("img");
        var n = (div1.childNodes.length)+1;
        img.setAttribute("id", "f"+ n)
        img.setAttribute("draggable","true")
        img.setAttribute("ondragstart", "drag(event)");
        img.setAttribute("class", "loadedfiles");
        img.src = window.URL.createObjectURL(selectedFiles[i]);
        img.height = 60;
        img.onload = function() {
       // window.URL.revokeObjectURL(this.src);
        
        }
        div1.appendChild(img);
     }

}



//veamos como pasamos las imagenes a un array y lo mandamos
document.getElementById("enviar").addEventListener("click", send)

async function send(){
var imagenes = div2.getElementsByTagName("img");
var imgarr = []
for(let i=0; i<imagenes.length; i++){
    let blob = await fetch(imagenes[i].src).then(r => r.blob());
    imgarr[i]=blob;
}

var form = new FormData();
for(let i=0; i<imgarr.length; i++){
    form.append("blob"+i, imgarr[i])
}
var title = document.getElementById('title').value

form.append("title", title)

	fetch("http://localhost:8080/NitroReader/EditChapter",{
		method: "POST",
		body: form
	}).then((response) => 	
		response.text()).then((data)=>{
            console.log(data)
        window.location.href ="http://localhost:8080/NitroReader/MangaInfo.html?manga="+localStorage.mangaid
        });
        
}



function getimgs(){
    fetch('http://localhost:8080/NitroReader/EditChapter?'+'currentChap='+localStorage.currentChap+"&mangaid="+localStorage.mangaid,{ method: 'GET',
    withCredentials: true,
    credentials: 'same-origin',
    headers:{
        'Content-Type': 'application/json' }}   )
    .then(res => res.json()).then(function(res){
        localStorage.setItem("chapter_id", res.data.chapter_id)
          if (res.data.title)  document.getElementById('title').value = res.data.title ;
          arr = Object.values(res.data)
          var ocurrences = arr.filter(val=> {if(val.indexOf('http://localhost:8080') != -1){return val}} )
            var count = ocurrences.length; 
            for(let i=1; i<=count ; i++){
            var d= document.createElement("div");
            var n = (div2.childNodes.length)+1;
            d.setAttribute("id", "z"+ n)
            d.setAttribute("class","border border-danger sisisi")
            d.setAttribute("ondrop", "dropcajas(event)")
            d.setAttribute("ondragover","allowDrop(event)")
            d.setAttribute("draggable","true")
            d.setAttribute("ondragstart", "drag(event)");
            div2.appendChild(d)
            var img = document.createElement("img");
            var n = (div2.childNodes.length)+1;
            img.setAttribute("id", "j"+ n)
            img.setAttribute("draggable","true")
            img.setAttribute("ondragstart", "drag(event)");
            img.setAttribute("class", "loadedfiles");
            img.src =res.data["direccion"+i]
            img.height = 60;
            d.appendChild(img)
        }
       
      });
}

document.getElementById("delchapter").addEventListener("click", deletechapter)
//funcion para borrar el capitulo
function deletechapter(){
    var init ={
        method: 'DELETE'
    }
    fetch('http://localhost:8080/NitroReader/Chapter?'+'currentChap='+localStorage.currentChap+"&mangaid="+localStorage.mangaid,init)
    .then(function(res){
        return res.json()
    }).then(function(res){
        localStorage.removeItem("currentChap");
        window.location.href ="MangaInfo.html?manga="+localStorage.mangaid;
    })
}

document.getElementById("gomangainfo").addEventListener("click", ()=> window.location.href ="http://localhost:8080/NitroReader/MangaInfo.html?manga="+localStorage.mangaid)