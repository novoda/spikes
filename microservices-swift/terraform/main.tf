# IBM
#
# Terraform IasC for Creating a VSI Image and deploying an Application into it
#
# Version 1.0
#
# Author: IBM

terraform {
    backend "local" {
        path = "repo/ibm-compute/terraform.tfstate"
    }
}

provider "ibm" {
  bluemix_api_key    = "${var.ibm_cloud_api_key}"
  softlayer_username = "${var.ibm_sl_username}"
  softlayer_api_key  = "${var.ibm_sl_api_key}"
}

resource "ibm_compute_ssh_key" "ssh_key_gip" {
    label = "${var.ssh_label}"
    public_key = "${var.ssh_public_key}"
}

resource "ibm_compute_vm_instance" "vm1" {
    hostname = "${var.vi_instance_name}"
    domain = "example.com"
    os_reference_code = "DEBIAN_8_64"
    datacenter = "${var.datacenter}"
    network_speed = 100
    hourly_billing = true
    private_network_only = false
    cores = 1
    memory = 1024
    disks = [25]
    local_disk = false
    ssh_key_ids = [
        "${ibm_compute_ssh_key.ssh_key_gip.id}"
    ]
}

# When the ibm_compute_bare_metal resource definition has a fixed_config_preset
# attribute, Terraform creates an hourly bare metal server. Hardware specifications
# are predefined in the fixed_config_preset attribute and cannot be modified.
# The following example shows you how to create a new hourly bare metal server.

# resource "ibm_compute_bare_metal" "hourly-bm1" {
#     hostname = "${var.bm_instance_name}"
#     domain = "example.com"
#     os_reference_code = "UBUNTU_16_64"
#     datacenter = "${var.datacenter}"
#     network_speed = 100 # Optional
#     hourly_billing = true # Optional
#     private_network_only = false # Optional
#     fixed_config_preset = "S1270_8GB_2X1TBSATA_NORAID"

#     user_metadata = "{\"value\":\"newvalue\"}" # Optional
#     tags = [
#       "collectd",
#       "mesos-master"
#     ]
#     notes = "note test"
# }
