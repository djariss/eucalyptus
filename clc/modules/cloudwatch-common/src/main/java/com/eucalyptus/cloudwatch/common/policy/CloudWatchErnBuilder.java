/*************************************************************************
 * Copyright 2009-2015 Ent. Services Development Corporation LP
 *
 * Redistribution and use of this software in source and binary forms,
 * with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer
 *   in the documentation and/or other materials provided with the
 *   distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ************************************************************************/
package com.eucalyptus.cloudwatch.common.policy;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.eucalyptus.auth.policy.ern.Ern;
import com.eucalyptus.auth.policy.ern.ServiceErnBuilder;
import net.sf.json.JSONException;

/**
 *
 */
public class CloudWatchErnBuilder extends ServiceErnBuilder {

  public static final Pattern RESOURCE_PATTERN = Pattern.compile( "([a-z0-9]+):(\\S+)" );

  public static final int ARN_PATTERNGROUP_CLOUDWATCH_TYPE = 1;
  public static final int ARN_PATTERNGROUP_CLOUDWATCH_ID = 2;

  public CloudWatchErnBuilder( ) {
    super( Collections.singleton( CloudWatchPolicySpec.VENDOR_CLOUDWATCH ) );
  }

  @Override
  public Ern build( final String ern,
                    final String service,
                    final String region,
                    final String account,
                    final String resource ) throws JSONException {
    final Matcher matcher = RESOURCE_PATTERN.matcher( resource );
    if ( matcher.matches( ) ) {
      String type = matcher.group( ARN_PATTERNGROUP_CLOUDWATCH_TYPE ).toLowerCase( );
      String id = matcher.group( ARN_PATTERNGROUP_CLOUDWATCH_ID ).toLowerCase( );
      return new CloudWatchResourceName( region, account, type, id );
    }
    throw new JSONException( "'" + ern + "' is not a valid ARN" );
  }
}
