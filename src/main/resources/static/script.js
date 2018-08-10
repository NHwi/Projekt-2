var modal = document.getElementById('myModal');

var btn = document.getElementById("login");

var span = document.getElementsByClassName("close")[0];
var menu = document.getElementById("dropmenu");

btn.onclick = function () {
    if (btn.classList.contains("account")) {
        if(menu.classList.contains("hidden")){
            menu.classList.remove("hidden");
        } else {
            menu.classList.add("hidden");
        }
    } else {
        modal.style.display = "block";
    }
}

span.onclick = function () {
    modal.style.display = "none";
}

window.onclick = function (event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

let loginbutton = document.getElementById('login-button');
let registerbutton = document.getElementById('register-button');
let registerForm = document.querySelector('.register-form');
let loginForm = document.querySelector('.login-form');

registerbutton.addEventListener('click', function (e) {
    registerForm.classList.remove('hidden');
    loginForm.classList.add('hidden');
    e.preventDefault();
});

loginbutton.addEventListener('click', function (e) {
    registerForm.classList.add('hidden');
    loginForm.classList.remove('hidden');
    e.preventDefault();
});

var objDiv = document.getElementById("box1");
objDiv.scrollTop = objDiv.scrollHeight;