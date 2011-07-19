# Copyright (c) 2011, Eucalyptus Systems, Inc.
# All rights reserved.
#
# Redistribution and use of this software in source and binary forms, with or
# without modification, are permitted provided that the following conditions
# are met:
#
#   Redistributions of source code must retain the above
#   copyright notice, this list of conditions and the
#   following disclaimer.
#
#   Redistributions in binary form must reproduce the above
#   copyright notice, this list of conditions and the
#   following disclaimer in the documentation and/or other
#   materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# Author: Mitch Garnaat mgarnaat@eucalyptus.com

from boto.roboto.awsqueryrequest import AWSQueryRequest
from boto.roboto.param import Param
import eucadmin

class DescribeServices(AWSQueryRequest):
  
    ServicePath = '/services/Bootstrap'
    ServiceClass = eucadmin.EucAdmin
    Description = 'Get services'
    Params = [
              Param(name='ListAll',
                    short_name='A',
                    long_name='all',
                    ptype='boolean',
                    default=False,
                    optional=True,
                    doc='Include all public service information.  Reported state information is determined by the view available to the target host, which should be treated as advisory (See documentation for guidance on interpreting this information).'),
              Param(name='ListInternal',
                    short_name=None,
                    long_name='system-internal',
                    ptype='boolean',
                    default=False,
                    optional=True,
                    doc='Include internal services information (note: this information is only for the target host).'),
              Param(name='ByServiceType',
                    short_name='T',
                    long_name='filter-type',
                    ptype='string',
                    optional=True,
                    doc='Filter services by component type.'),
              Param(name='ByHost',
                    short_name='H',
                    long_name='filter-host',
                    ptype='string',
                    optional=True,
                    doc='Filter services by host.'),
              Param(name='ByState',
                    short_name='F',
                    long_name='filter-state',
                    ptype='string',
                    optional=True,
                    doc='Filter services by state.'),
              Param(name='ByPartition',
                    short_name='P',
                    long_name='filter-partition',
                    ptype='string',
                    optional=True,
                    doc='Filter services by partition.'),
              Param(name='ShowEvents',
                    short_name='E',
                    long_name='events',
                    ptype='boolean',
                    default=False,
                    optional=True,
                    doc='Show service event details.'),
              Param(name='ShowEventStacks',
                    long_name='events-verbose',
                    ptype='boolean',
                    default=False,
                    optional=True,
                    doc='Show verbose service event details.')
              ]

    def __init__(self, **args):
        AWSQueryRequest.__init__(self, **args)
        self.list_markers = ['euca:serviceStatuses']
        self.item_markers = ['euca:item']
  
    def get_connection(self, **args):
        if self.connection is None:
            args['path'] = self.ServicePath
            self.connection = self.ServiceClass(**args)
        return self.connection
      
    def cli_formatter(self, data):
        services = getattr(data, 'euca:serviceStatuses')
        fmt = 'SERVICE\t%-15.15s\t%-15s\t%-15s\t%-10s\t%-4s\t%-40s\t%s'
        detail_fmt = 'SERVICEEVENT\t%-36.36s\t%s'
        for s in services:
            service_id = s['euca:serviceId']
            print fmt % (service_id['euca:type'],
                         service_id['euca:partition'],
                         service_id['euca:name'],
                         s['euca:localState'],
                         s['euca:localEpoch'],
                         service_id['euca:uri'],
                         service_id['euca:fullName'])
            details = s['euca:statusDetails']
            if details:
                detail_item = details['euca:item']
                if detail_item:
                      print detail_fmt % (detail_item['euca:uuid'],
                                          detail_item['euca:serviceFullName'])
                      print detail_fmt % (detail_item['euca:uuid'],
                                          detail_item['euca:severity'])
                      print detail_fmt % (detail_item['euca:uuid'],
                                          detail_item['euca:timestamp'])
                      print detail_fmt % (detail_item['euca:uuid'],detail_item['euca:message'])
                      if detail_item['euca:stackTrace']:
                          print detail_item['euca:stackTrace']
                      print
                             


    def main(self, **args):
        return self.send(**args)

    def main_cli(self):
        self.do_cli()
    
