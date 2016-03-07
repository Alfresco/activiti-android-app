/*
 *  Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco Activiti Mobile for Android.
 *
 * Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.activiti.android.platform.exception;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.activiti.android.app.R;
import com.activiti.android.platform.utils.ConnectivityUtils;

/**
 * Created by jpascal on 19/03/2015.
 */
public class ExceptionMessageUtils
{
    private ExceptionMessageUtils()
    {
    }

    public static boolean isGeneralError(Throwable error)
    {
        /*
         * if (error.getResponse() != null) { int httpStatus =
         * error.getResponse().getStatus(); switch (httpStatus) { case
         * HttpStatus.SC_UNAUTHORIZED: return true; } }
         */
        return false;
    }

    public static String getMessage(Context context, Throwable error)
    {
        Integer message = null;
        /*
         * if (error.getResponse() != null) { int httpStatus =
         * error.getResponse().getStatus(); switch (httpStatus) { case
         * HttpStatus.SC_UNAUTHORIZED: message =
         * R.string.app_error_unauthorized; break; } if (message != null) {
         * return context.getString(message); } }
         */

        message = R.string.app_error_generic;
        try
        {
            throw error.getCause();
        }
        catch (SocketTimeoutException e)
        {
            message = R.string.app_error_bad_request;
        }
        catch (Throwable e)
        {
            message = R.string.app_error_unknown;
        }
        return context.getString(message);
    }

    public static int getSignInMessageId(Context context, Throwable error)
    {
        int messageId = R.string.error_session_creation;
        try
        {
            throw error;
        }
        catch (ConnectException e)
        {
            messageId = R.string.error_session_nodata;
        }
        catch (NetworkErrorException e)
        {
            messageId = R.string.error_session_nodata;
        }
        catch (UnknownHostException e)
        {
            if (ConnectivityUtils.hasInternetAvailable(context))
            {
                messageId = R.string.error_session_hostname;
            }
            else
            {
                messageId = R.string.error_session_nodata;
            }
        }
        catch (SocketTimeoutException e)
        {
            messageId = R.string.app_error_bad_request;
        }
        catch (SSLHandshakeException e)
        {
            messageId = R.string.error_session_ssl;
            if ((e.getCause() instanceof CertPathValidatorException || e.getCause() instanceof CertificateException)
                    && e.getCause().getCause().getMessage().contains("Trust anchor for certification path not found."))
            {
                messageId = R.string.error_session_certificate;
            }
            else if ((e.getCause() instanceof CertificateException)
                    && e.getCause().getCause().getMessage().contains("Could not validate certificate: current time:"))
            {
                messageId = R.string.error_session_certificate_expired;
            }
            else if (e.getCause() instanceof CertificateExpiredException
                    || e.getCause() instanceof CertificateNotYetValidException)
            {
                messageId = R.string.error_session_certificate_expired;
            }
            else if (e.getCause() instanceof CertificateExpiredException)
            {
                messageId = R.string.error_session_certificate;
            }

        }
        catch (SSLProtocolException e)
        {
            messageId = R.string.error_session_ssl;
        }
        catch (Throwable e)
        {
            messageId = R.string.error_session_creation;
        }
        return messageId;
    }

}
