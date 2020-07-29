package vmdb

def toList(arg){
    def output=[]
    arg.split(' ').each { 
        output.push(it)
    }
    return output    
}

//input variables in env:
// MAXQTY, CORELIST, RAMLIST, DISKnLIST (1-5)
def vmInputMessage() {
	//env.getEnvironment().each{ k,v -> echo "${k} is ${v}"}
	def params=[]
	def qtyString=[]
	for(int i in 1..env.MAXQTY.toInteger()) {
		qtyString.push("${i}")
	}
	params.push(string(defaultValue: '', description: 'Your SSO id', name: 'SSO', trim:true))
	params.push(choice(choices: ["30 days","60 days","90 days","120 days","180 days","0 days-forever"], description: 'Days to keep VM', name: 'Lifetime'))
	params.push(choice(choices: ["A-Axone","C-CareScape","I-IB","O-Other-GEHC","S-Springdale","V-Validation or Verification","Z-Infrastructure"], description: 'Choose Project for this VM', name: 'Name1'))
	params.push(choice(choices: ["I-Individual","D-Development","P-Production","T-Testing"], description: 'Select primary intent for this VM', name: 'Name2'))
	params.push(choice(choices: ["I-Normal Use","B-Build","C-CPU Intensive","D-Database or Disk Intensive"], description: 'Select primary use of this VM', name: 'Name3'))
	params.push(choice(choices: qtyString, description:'Number of VMs to create', name:'QTY'))
	params.push(choice(choices: toList(env.CORELIST), description:'Number of Cores', name: 'Cores'))
	params.push(choice(choices: toList(env.RAMLIST), description: 'RAM, in GB', name: 'RAMSize'))
	params.push(choice(choices: toList(env.DISK1LIST), description: 'Size of disk', name:'disk1Size'))
	if(env.disk2List) {params.push(choice(choices: toList(env.disk2List), description: 'Size of disk 2', name:'disk2Size'))}
	if(env.disk3List) {params.push(choice(choices: toList(env.disk3List), description: 'Size of disk 3', name:'disk3Size'))}
	if(env.disk4List) {params.push(choice(choices: toList(env.disk4List), description: 'Size of disk 4', name:'disk4Size'))}
	if(env.disk5List) {params.push(choice(choices: toList(env.disk5List), description: 'Size of disk 5', name:'disk5Size'))}
	def vmData=input message: 'Select Options', parameters: params
	vmData.each { $k, $v ->
		env.setProperty($k,$v)
	}
}
