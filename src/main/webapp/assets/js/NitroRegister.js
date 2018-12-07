var loginbtn = document.getElementById("loginbtn");
var registerbtn= document.getElementById("registerbtn");
var logoutbtn = document.getElementById("logoutbtn");

//los eventos para el redireccionado



registerbtn.addEventListener("click", function(){window.location.href ="NitroRegister.html"})
document.getElementById("homebtn").addEventListener("click", function(){window.location.href ="index.html"})
document.getElementById("gohome").addEventListener("click", function(){window.location.href ="index.html"})
loginbtn.addEventListener("click", function(){window.location.href ="NitroLogin.html"})
document.getElementById("relogin").addEventListener("click", function(){window.location.href ="NitroLogin.html"})


document.getElementById("create").addEventListener("click", registrar)
document.addEventListener("keyup",keyU);

    function keyU(e){
        if (e.keyCode == 13 ){
            if(document.activeElement == document.getElementById("name") || (document.activeElement == document.getElementById("user")) || (document.activeElement == document.getElementById("pwd")) || (document.activeElement == document.getElementById("rpwd"))){
                registrar()
            }
        }
    }



    function registrar(){
        var pwd = document.getElementById("pwd").value
        var rpwd = document.getElementById("rpwd").value
        var mail = document.getElementById("email").value
        var user = document.getElementById("user").value
        var name = document.getElementById("name").value
        if(pwd === rpwd){
        var url = 'http://localhost:8080/NitroReader/Register';
        var data = {name: name,
                    password : pwd,
                    mail: mail,
                    user: user
                                };
    
        fetch(url, {
        method: 'POST', 
        body: JSON.stringify(data), 
        headers:{
            'Content-Type': 'application/json'
        }
        }).then(res => res.json()).then(function(res){
            if(res.status ==200){
                console.log("Register complete!")
                window.location.href ="index.html"
            }else{
                alert("hubo un error en el registro, verifique sus datos")
            }
        })
        .catch(error => console.log('Error:', error))
            }else{alert("passwords don't match!")}
    
    }


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
        .catch(error => console.log('Error:', error))
    })








