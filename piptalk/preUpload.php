<?php
    //this function returns a random 5-char filename with the png extension
	

	$FileName = $_POST["FileNme"];
	$target = 'images/';
    $target = $target . $FileName;
	
 if(move_uploaded_file($_FILES['image']['tmp_name'], $target))

 {
	 
	 echo json_encode(array ("response"=>"sucsses"));
	
}
else {
echo json_encode(array ("response"=>"false"));

}

?>