<?php

$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();

}else{
	$user_id=$_POST["user_id"];
	$pass=$_POST["password"];
	
	
	if($stmt=$Con->prepare("UPDATE  users SET password=? WHERE id=? ; ")){
		$stmt->bind_param('si',$pass,$user_id);
		if($stmt->execute()){
		    $stmt->store_result();
				$temp=array('response'=>'success');
				echo json_encode($temp);
			
		}else{
			echo "execute failed";
		}
	}else{
		echo "prepare failed";
	}
}

?>