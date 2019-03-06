function toggle(state){
    console.log("state toggled");
    let signIn = document.querySelector("#sign-in");
	let signUp = document.querySelector("#sign-up");

        //register is visible and should be hidden
        console.log(state.toggled)
		if(state.toggled){
            signUp.classList.add("hidden");
            signIn.classList.remove("hidden");
		} else {
            signUp.classList.remove("hidden");
			signIn.classList.add("hidden");
		}
			
		state.toggled = !state.toggled
}