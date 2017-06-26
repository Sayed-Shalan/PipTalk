<?php

$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();

}else{
	$user_id=$_POST["user_id"];
	$lang=$_POST["lang"];
	
	$query="UPDATE user_info SET native_language='$lang' WHERE user_id='$user_id'; ";
	if($stmt=$Con->prepare($query)){
		if($stmt->execute()){
		    $stmt->store_result();
				$temp=array('response'=>'success');
				echo json_encode($temp);
		}else{
			$temp=array('response'=>'failed_execute');
				echo json_encode($temp);
		}
	}else{
		$temp=array('response'=>'failed_prepare');
				echo json_encode($temp);
	}
}

?>