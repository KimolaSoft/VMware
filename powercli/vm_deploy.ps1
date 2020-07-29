
function EnvCheck()
{
	# Verify environment variables
	if($ENV:SSO -match "^\\d{9}$") {
	  echo "SSO is $ENV:SSO"
	} else {
	  throw "Invalid SSO"
	}
	$Lifetime = $ENV:Lifetime.split(' ')[0]
	echo "Lifetime=$Lifetime" >foo.bar
	echo "VM will last for $Lifetime days!"
	echo "Prefix=$($ENV:Name1.Substring(0,1))$($ENV:Name2.Substring(0,1))$($ENV:Name3.Substring(0,1))">>foo.bar
}

function vmCreate()
{
	Get-Content foo.bar | Where-Object {$_.length -gt 0} | Where-Object {!$_.StartsWith("#")} | ForEach-Object {
		$var = $_.Split('=',2).Trim()
		#echo "$($var[0]) $($var[1])"
		New-Variable -Scope Script -Name $var[0] -Value $var[1]
	}
	echo "Creating VM $env:TEMPLATE with $env:Cores, $env:RAMSize GB"
	$vmname="$($Prefix)-$($env:SHORTNAME)-test2"
	echo $vmname
throw 'stop here'
	Connect-VIServer -Server cloud.tower.am.health.ge.com
	$template = Get-Template -Name $env:TEMPLATE
	$pool = Get-ResourcePool -Name $env:POOL
	$ds = Get-Datastore -name $env:DS
	$fold = Get-Folder -type Datacenter
	$env:FOLDER.Split('/')|ForEach-Object -Process {$fold=Get-Folder $_ -location $fold}
	$VM = New-VM -Name $vmname -Template $template -ResourcePool $pool -Datastore $ds -Location $fold
	Set-VM $VM -NumCpu $env:Cores -MemoryGB $env:RAMSize -Confirm:$false
	$shh=Set-Annotation $VM -CustomAttribute 'Owner-SSO' "$ENV:SSO"
	$date = Get-Date -Format 'yyyy-MM-dd'
	$shh=Set-Annotation $VM -CustomAttribute 'Date-Created' $date
	$expires = (Get-Date).AddDays($env:Lifetime).ToString("yyyy-MM-dd")
	$shh=Set-Annotation $VM -CustomAttribute 'Date-Expires' $expires

	# Cleanup
	Disconnect-VIServer -Server cloud.tower.am.health.ge.com -Force -Confirm:$False
}

