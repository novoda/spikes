# Default Data Center for the creation of the Virtual Instance
# Find a list of available datacenters here:
# https://www.ibm.com/cloud-computing/bluemix/data-centers
variable "datacenter" {
  description = "Washington"
  default = "wdc04"
}

# SSH Label - this does not need to be changed
variable "ssh_label" {
 	default = "PublicKey"
}

# SSH Public Key - update this via environment variable within the DevOps pipeline
# For more information, please see the README
variable "ssh_public_key" {
	default = "publickey"
}

# IBM Cloud API Key - update this via environment variable within the DevOps pipeline
# For more information, please see the README
variable "ibm_cloud_api_key" {
	default = "ibmcloudapikey"
}

# IBM SoftLayer API Key - update this via environment variable within the DevOps pipeline
# For more information, please see the README
variable "ibm_sl_api_key" {
	default = "ibmslapikey"
}

# IBM SoftLayer Username - update this via environment variable within the DevOps pipeline
# For more information, please see the README
variable "ibm_sl_username" {
	default = "ibmslusername"
}

# Name of the Virtual Instance based on the App Name
# Update this via environment variable within the DevOps pipeline
# For more information, please see the README
variable "vi_instance_name" {
	default = "microservices-swift-01"
}
