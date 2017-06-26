<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
}
else{
	$user_name= $_POST['username'];
	$query="SELECT id FROM users WHERE user_name='$user_name' ";
	if($stmt=$Con->prepare($query)){
		if($stmt->execute()){
			$stmt->store_result();
			$stmt->bind_result($id);
			$stmt->fetch();
			$userinfo=array('response'=>"Done",'id'=>$id);
				echo json_encode($userinfo);
			
		}else{
			echo 'execute failed';
		}
	}else{
		echo 'prepare failed';
	}
}

?>