<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
	
}

else
{
	$username = $_POST["username"];
	$userpass = $_POST["pass"];
	  
		if($stmt=$Con->prepare("SELECT * FROM users WHERE user_name=? AND password=? ;"))
		{
			 $stmt->bind_param('ss',$username,$userpass);
			
		if($stmt->execute())
		{
				$stmt->store_result();
			$count = $stmt->num_rows();
		
			if($count==1)
			{
				$stmt->bind_result($id,$user_name,$user_password,$user_token,$user_state,$admin);
			$stmt->fetch();
					
					if($stmt=$Con->prepare("SELECT * FROM user_info WHERE user_id=? ;")){
						$stmt->bind_param('i',$id);
						if($stmt->execute()){
							$stmt->store_result();
							$count=$stmt->num_rows();
							if($count==1){
								$stmt->bind_result($id2,$lang,$gender,$has_image,$status,$phone,$birth_date);
								$stmt->fetch();
								$temp = array ('id'=>$id, 
								'user_name'=>$user_name ,
								'user_password'=>$user_password , 
								'user_token'=>$user_token,
								'user_state'=>$user_state,
								'is_admin'=>$admin,'native_language'=>$lang,'gender'=>$gender,'has_image'=>$has_image,'status'=>$status,'phone_number'=>$phone,'date_of_birth'=>$birth_date);
								$userinfo = array('response'=>'success', 'user'=>$temp);
					echo json_encode($userinfo);
							}
						}
					}
					
				
			}
			else
			{
				$userinfo['response']="not found";
				echo json_encode($userinfo);
			}
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