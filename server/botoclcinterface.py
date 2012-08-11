import boto
import json

from .botojsonencoder import BotoJsonEncoder
from .clcinterface import ClcInterface


# This class provides an implmentation of the clcinterface using boto
class BotoClcInterface(ClcInterface):
    conn = None
    saveclcdata = False

    def __init__(self, clc_host, access_id, secret_key):
        #boto.set_stream_logger('foo')
        self.conn = boto.connect_euca(host=clc_host,
                                aws_access_key_id=access_id,
                                aws_secret_access_key=secret_key, debug=0)
        self.conn.APIVersion = '2012-03-01'

    def __save_json__(self, obj, name):
        f = open(name, 'w')
        json.dump(obj, f, cls=BotoJsonEncoder, indent=2)
        f.close()

    def get_all_zones(self):
        obj = self.conn.get_all_zones()
        if self.saveclcdata:
            self.__save_json__(obj, "mockdata/Zones.json")
        return obj

    def get_all_images(self):
        obj = self.conn.get_all_images()
        if self.saveclcdata:
            self.__save_json__(obj, "mockdata/Images.json")
        return obj

    # returns list of image attributes
    def get_image_attribute(self, image_id, attribute):
        return self.conn.get_image_attribute(image_id, attribute)

    # returns True if successful
    def modify_image_attribute(self, image_id, attribute, operation, users, groups):
        return self.conn.modify_image_attribute(image_id, attribute, operation, users, groups)

    # returns True if successful
    def reset_image_attribute(self, image_id, attribute):
        return self.conn.reset_image_attribute(image_id, attribute)

    def get_all_instances(self):
        obj = self.conn.get_all_instances()
        if self.saveclcdata:
            self.__save_json__(obj, "mockdata/Instances.json")
        return obj

    def run_instances(self, image_id, min_count=1, max_count=1,
                      key_name=None, security_groups=None,
                      user_data=None, addressing_type=None,
                      instance_type='m1.small', placement=None,
                      kernel_id=None, ramdisk_id=None,
                      monitoring_enabled=False, subnet_id=None,
                      block_device_map=None,
                      disable_api_termination=False,
                      instance_initiated_shutdown_behavior=None,
                      private_ip_address=None,
                      placement_group=None, client_token=None,
                      security_group_ids=None,
                      additional_info=None, instance_profile_name=None,
                      instance_profile_arn=None, tenancy=None):
        return self.conn.run_instances(image_id, min_count, max_count,
                      key_name, security_groups,
                      user_data, addressing_type,
                      instance_type, placement,
                      kernel_id, ramdisk_id,
                      monitoring_enabled, subnet_id,
                      block_device_map,
                      disable_api_termination,
                      instance_initiated_shutdown_behavior,
                      private_ip_address,
                      placement_group, client_token,
                      security_group_ids,
                      additional_info, instance_profile_name,
                      instance_profile_arn, tenancy)

    # returns instance list
    def terminate_instances(self, instance_ids):
        return self.conn.terminate_instances(instance_ids)

    # returns instance list
    def stop_instances(self, instance_ids, force=False):
        return self.conn.stop_instances(instance_ids, force)

    # returns instance list
    def start_instances(self, instance_ids):
        return self.conn.start_instances(instance_ids)

    # returns instance status
    def reboot_instances(self, instance_ids):
        return self.conn.reboot_instances(instance_ids)

    # returns console output
    def get_console_output(self, instance_id):
        return self.clc.get_console_output(instance_id)

    def get_all_addresses(self):
        obj = self.conn.get_all_addresses()
        if self.saveclcdata:
            self.__save_json__(obj, "mockdata/Addresses.json")
        return obj

    # returns address info
    def allocate_address(self):
        return self.conn.allocate_address()

    # returns True if successful
    def release_address(self, publicip):
        return self.conn.release_address(publicip)

    # returns True if successful
    def associate_address(self, publicip, instanceid):
        return self.conn.associate_address(publicip, instanceid)

    # returns True if successful
    def disassociate_address(self, publicip):
        return self.conn.disassociate_address(publicip)

    def get_all_key_pairs(self):
        obj = self.conn.get_all_key_pairs()
        if self.saveclcdata:
            self.__save_json__(obj, "mockdata/Keypairs.json")
        return obj

    # returns keypair info and key
    def create_key_pair(self, key_name):
        return self.conn.create_key_pair(key_name)

    # returns nothing
    def delete_key_pair(self, key_name):
        return self.conn.delete_key_pair(key_name)

    def get_all_security_groups(self):
        obj = self.conn.get_all_security_groups()
        if self.saveclcdata:
            self.__save_json__(obj, "mockdata/Groups.json")
        return obj

    # returns True if successful
    def create_security_group(self, name, description):
        return self.conn.create_security_group(name, description)

    # returns True if successful
    def delete_security_group(self, name=None, group_id=None):
        return self.conn.delete_security_group(name, group_id)

    # returns True if successful
    def authorize_security_group(self, name=None,
                                 src_security_group_name=None,
                                 src_security_group_owner_id=None,
                                 ip_protocol=None, from_port=None, to_port=None,
                                 cidr_ip=None, group_id=None,
                                 src_security_group_group_id=None):
        return self.conn.authorize_security_group_deprecated(name, 
                                 src_security_group_name,
                                 src_security_group_owner_id,
                                 ip_protocol, from_port, to_port,
                                 cidr_ip)#, group_id,
                                 #src_security_group_group_id)

    # returns True if successful
    def revoke_security_group(self, name=None,
                                 src_security_group_name=None,
                                 src_security_group_owner_id=None,
                                 ip_protocol=None, from_port=None, to_port=None,
                                 cidr_ip=None, group_id=None,
                                 src_security_group_group_id=None):
        return self.conn.revoke_security_group_deprecated(name,
                                 src_security_group_name,
                                 src_security_group_owner_id,
                                 ip_protocol, from_port, to_port,
                                 cidr_ip)#, group_id,
                                 #src_security_group_group_id)

    def get_all_volumes(self):
        obj = self.conn.get_all_volumes()
        if self.saveclcdata:
            self.__save_json__(obj, "mockdata/Volumes.json")
        return obj

    # returns volume info
    def create_volume(self, size, availability_zone, snapshot_id):
        return self.conn.create_volume(size, availability_zone, snapshot_id)

    # returns True if successful
    def delete_volume(self, volume_id):
        return self.conn.delete_volume(volume_id)

    # returns True if successful
    def attach_volume(self, volume_id, instance_id, device):
        return self.conn.attach_volume(volume_id, instance_id, device)

    # returns True if successful
    def detach_volume(self, volume_id, instance_id, device, force=False):
        return self.conn.detach_volume(volume_id, instance_id, device, force)

    def get_all_snapshots(self):
        obj = self.conn.get_all_snapshots()
        if self.saveclcdata:
            self.__save_json__(obj, "mockdata/Snapshots.json")
        return obj

    # returns snapshot info
    def create_snapshot(self, volume_id, description):
        return self.conn.create_snapshot(volume_id, description)

    # returns True if successful
    def delete_snapshot(self, snapshot_id):
        return self.conn.delete_snapshot(snapshot_id)

    # returns list of snapshots attributes
    def get_snapshot_attribute(self, snapshot_id, attribute):
        return self.conn.get_snapshot_attribute(snapshot_id, attribute)

    # returns True if successful
    def modify_snapshot_attribute(self, snapshot_id, attribute, operation, users, groups):
        return self.conn.modify_snapshot_attribute(snapshot_id, attribute, operation, users, groups)

    # returns True if successful
    def reset_snapshot_attribute(self, snapshot_id, attribute):
        return self.conn.reset_snapshot_attribute(snapshot_id, attribute)
