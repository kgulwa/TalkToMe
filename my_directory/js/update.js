var ul = document.getElementById("languages");

ul.onclick = function(event) {
  var language = event.target.innerHTML;
  var selection = document.getElementById("selection");
  selection.innerHTML = "You chose " + language;
}