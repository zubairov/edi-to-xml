/* Author: Renat Zubairov 

*/

$(document).ready( function(){
 
  // bind "click" event for links with title="submit" 
  $("#convert").click( function(){
    $(this).parents("form").submit();
  });
 
});






















