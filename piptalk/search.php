<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
	
}

else
{
	$username = $_POST["username"];
	
	  
		if($stmt=$Con->prepare("SELECT id,user_name FROM users WHERE user_name LIKE ? "))
		{
			$username=$username.'%';
			$stmt->bind_param('s',$username);
		if($stmt->execute())
		{
				$stmt->store_result();
			$count = $stmt->num_rows();
			$feed = array();
				$stmt->bind_result($id,$user_name);
				while($stmt->fetch())
				{
					$query2="SELECT has_image FROM user_info WHERE user_id='$id'";
					if($stmt1=$Con->prepare($query2)){
						if($stmt1->execute()){
							$stmt1->store_result();
							$count=$stmt1->num_rows();
							if($count==1){
								$stmt1->bind_result($has_image);
							$stmt1->fetch();
							$temp = array ('id'=>$id, 'username'=>$user_name ,'has_image'=>$has_image );
					        array_push($feed,$temp);
							}else{
								echo 'count not equal one';
							}
							
						}else{
							echo 'execute 2 failed';
						}
					}else{
						echo 'prepare 2 failed';
					}
					
					
				}
				$userinfo = array('response'=>'success', 'user'=>$feed);
			echo json_encode($userinfo);
			
		}
		else
		{
			echo"execute failed";
		}
	}
	else
	{
		echo"prepare failed";
	}
}
?>