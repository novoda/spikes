output "host ip"{
	value = "${ibm_compute_vm_instance.vm1.ipv4_address}"
}
