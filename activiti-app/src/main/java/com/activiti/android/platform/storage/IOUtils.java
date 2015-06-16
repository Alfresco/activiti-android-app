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

package com.activiti.android.platform.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jpascal on 01/04/2015.
 */
public class IOUtils
{

    public static void closeStream(Closeable stream)
    {
        if (stream != null)
        {
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                // Ignore
            }
        }
    }

    public static void ensureOrCreatePathAndFile(File contentFile)
    {
        contentFile.getParentFile().mkdirs();
        createUniqueName(contentFile);
    }

    private static File createUniqueName(File file)
    {
        if (!file.exists()) { return file; }

        int index = 1;

        File tmpFile = file;
        while (index < 500)
        {
            tmpFile = new File(tmpFile.getParentFile(), tmpFile.getName() + "-" + index);
            if (!tmpFile.exists()) { return tmpFile; }
            index++;
        }
        return null;
    }

    public static boolean copyFile(InputStream src, File dest) throws IOException
    {
        ensureOrCreatePathAndFile(dest);
        return copyStream(src, new FileOutputStream(dest));
    }

    public static final int MAX_BUFFER_SIZE = 1024;

    public static boolean copyStream(InputStream src, OutputStream osstream) throws IOException
    {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        boolean copied = true;

        try
        {

            bos = new BufferedOutputStream(osstream);
            bis = new BufferedInputStream(src);

            byte[] buffer = new byte[MAX_BUFFER_SIZE];

            int count;
            while ((count = bis.read(buffer)) != -1)
            {
                bos.write(buffer, 0, count);
            }
            bos.flush();
        }
        catch (IOException e)
        {
            copied = false;
            throw e;
        }
        finally
        {
            closeStream(osstream);
            closeStream(src);
            closeStream(bis);
        }
        return copied;
    }

    public static InputStream getContentFileInputStream(File contentFile)
    {

        try
        {
            if (contentFile != null)
            {
                BufferedInputStream mb = new BufferedInputStream(new FileInputStream(contentFile));
                return mb;
            }
        }
        catch (FileNotFoundException e)
        {
        }
        return null;
    }

    public static File createFolder(File f, String extendedPath)
    {
        File tmpFolder = null;
        tmpFolder = new File(f, extendedPath);
        if (!tmpFolder.exists())
        {
            tmpFolder.mkdirs();
        }

        return tmpFolder;
    }

    public static byte[] getBytesFromStream(InputStream is) throws IOException
    {
        int len;
        int size = 1024;
        byte[] buf;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = is.read(buf, 0, size)) != -1)
        {
            bos.write(buf, 0, len);
        }
        buf = bos.toByteArray();

        return buf;
    }

    public static void saveBytesToFile(byte[] bytes, String path)
    {
        FileOutputStream fileOuputStream = null;
        try
        {
            fileOuputStream = new FileOutputStream(path);
            fileOuputStream.write(bytes);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeStream(fileOuputStream);
        }
    }

    public static void saveBytesToStream(byte[] bytes, OutputStream stream)
    {
        try
        {
            stream.write(bytes);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeStream(stream);
        }
    }

}
