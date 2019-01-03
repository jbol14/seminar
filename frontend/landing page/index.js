let state = {
    signin : true,
    validEmail: false,
    validPass: false
}
function toggleSign(){
    let p = document.getElementById('sign-text');
    let button = document.getElementById('btn-sign');
    let signup = document.getElementById('signup');
    let signin = document.getElementById('signin');

    if(state.signin){
        p.textContent = "Have an account?";
        button.textContent = "Sign In!"
        signin.style.display = "none";
        signup.style.display = "block";
    }
    else{
        p.textContent = "Not a member?";
        button.textContent = "Sign Up!"
        signin.style.display = "block";
        signup.style.display = "none";
    }

    state.signin = !state.signin;
}

function validatePassword(){
    let pass1 = document.getElementById("signup-password-1");
    let pass2 = document.getElementById("signup-password-2");

    if(pass1.value === pass2.value && pass1.value.length > 5){
        pass1.classList.remove('is-invalid');
        pass2.classList.remove("is-invalid");
        pass1.classList.add("is-valid")
        pass2.classList.add("is-valid")
        state.validPass = true;
    }else{
        pass1.classList.add('is-invalid');
        pass2.classList.add("is-invalid");
        pass1.classList.remove("is-valid")
        pass2.classList.remove("is-valid")
        state.validPass = false;
    }

}

function validateEmail(){
    let email = document.getElementById('signup-email');

    if(email.value.includes('@') && email.value.includes('.')){
        email.classList.remove('is-invalid');
        email.classList.add('is-valid');
        state.validEmail = true;
    }else{
        email.classList.remove('is-valid');
        email.classList.add('is-invalid');
        state.validEmail = false;
    }
}

function signIn(){
    const username = document.getElementById('signin-username').value;
    const password = document.getElementById('signin-password').value;

    const concat = window.btoa(`${username}:${password}`);

    const xhr = new XMLHttpRequest();

    xhr.open('GET', 'http://127.0.0.1:8080/login');

    xhr.setRequestHeader('authorization',"Basic " + concat);

    xhr.onload = function(){
        if(xhr.status === 200){
            const token = xhr.response;
            console.log(token);
            alert(`Token received: ${token}`);
        }
    }

    xhr.send();
}

function signUp(){
    if(state.validEmail && state.validPass){
        const xhr = new XMLHttpRequest();

        const params = `name=${document.getElementById('signup-username').value}&email=${document.getElementById('signup-email').value}&password=${document.getElementById('signup-password-1').value}`

        console.log(params)

        xhr.open('POST','http://127.0.0.1:8080/users');
        xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");

        xhr.onload = function(){
            if(xhr.status === 200){
                alert('Account created')
            }
        }

        xhr.send(params)
    }else{
        alert('Invalid credentials');
    }
}