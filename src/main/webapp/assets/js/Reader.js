var mainimg = document.getElementById("mainimg")
var max;
var filedir
var currentP= 1
var chapters;
var readerChapter;


 function getchapter(){
    var init ={
        method: 'GET'
    }
    fetch('http://localhost:8080/NitroReader/Chapter?option=getchapter&currentChap='+localStorage.currentChap+"&mangaid="+localStorage.mangaid,init)
    .then(function(res){
        return res.json()
    }).then(function(res){
        console.log(res)
        max = res.max;
        if(max>0){
            if(localStorage.retrocediendo === "si"){
                currentP = max
                filedir= res.filedir;
                mainimg.setAttribute("src", filedir+"/"+ currentP+".png");
            }else{
                currentP=1
                filedir= res.filedir;
                 mainimg.setAttribute("src", filedir+"/"+ currentP+".png");
            }
            
        }else{
            alert("este capitulo se encuentra vacio!")
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
                    localStorage.setItem("retrocediendo", "si") 
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
                    localStorage.setItem("retrocediendo", "no") 
                    alert("capitulo finalizado, ha sido redirigido al siguiente capitulo")
                    localStorage.setItem("currentChap", chapters["nombre"+(readerChapter+1)])
                     window.location.href ="Reader.html"
                }else{
                    alert("capitulo finalizado, ha llegado al final del manga.")
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
    }).then(function(res){
        var count = Object.keys(res).length;
        chapters = res;
        for(let i=0; i<count ; i++){
            let a = document.createElement("a");
            a.setAttribute("id", "capitulo"+ res["nombre"+i])
            a.setAttribute("class", "dropdown-item")
            a.setAttribute("href", "#")
            a.textContent = res["nombre"+i];
            a.addEventListener("click",function(){
                localStorage.setItem("currentChap", res["nombre"+i])
                window.location.href ="Reader.html"
                 })
            document.getElementById("dropmenu").appendChild(a);
        }
        
    })
    
    
}

ordenChap = function(){
    for(let i=0; i<Object.keys(chapters).length; i++){
        if(localStorage.currentChap == chapters["nombre"+i]){
            readerChapter = i;
        }
    }
}