var ul = document.getElementById("languages");		

ul.onclick = function(event) {				
  var li = event.target;				
  alert(li.innerHTML);					
}