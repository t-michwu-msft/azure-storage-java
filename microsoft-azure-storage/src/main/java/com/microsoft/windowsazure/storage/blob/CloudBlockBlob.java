/**
 * Copyright Microsoft Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoft.windowsazure.storage.blob;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import com.microsoft.windowsazure.storage.AccessCondition;
import com.microsoft.windowsazure.storage.Constants;
import com.microsoft.windowsazure.storage.DoesServiceRequest;
import com.microsoft.windowsazure.storage.OperationContext;
import com.microsoft.windowsazure.storage.StorageException;
import com.microsoft.windowsazure.storage.StorageUri;
import com.microsoft.windowsazure.storage.core.Base64;
import com.microsoft.windowsazure.storage.core.ExecutionEngine;
import com.microsoft.windowsazure.storage.core.RequestLocationMode;
import com.microsoft.windowsazure.storage.core.SR;
import com.microsoft.windowsazure.storage.core.StorageRequest;
import com.microsoft.windowsazure.storage.core.StreamMd5AndLength;
import com.microsoft.windowsazure.storage.core.Utility;

/**
 * Represents a blob that is uploaded as a set of blocks.
 */
public final class CloudBlockBlob extends CloudBlob {

    /**
     * Creates an instance of the <code>CloudBlockBlob</code> class using the specified relative URI and storage service
     * client.
     * 
     * @param uri
     *            A <code>java.net.URI</code> object that represents the relative URI to the blob, beginning with the
     *            container name.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public CloudBlockBlob(final URI uri) throws StorageException {
        this(new StorageUri(uri));
    }

    /**
     * Creates an instance of the <code>CloudBlockBlob</code> class using the specified relative URI and storage service
     * client.
     * 
     * @param uri
     *            A <code>StorageUri</code> object that represents the relative URI to the blob, beginning with the
     *            container name.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public CloudBlockBlob(final StorageUri uri) throws StorageException {
        super(BlobType.BLOCK_BLOB);

        Utility.assertNotNull("blobAbsoluteUri", uri);
        this.setStorageUri(uri);
        this.parseURIQueryStringAndVerify(uri, null, Utility.determinePathStyleFromUri(uri.getPrimaryUri(), null));
    }

    /**
     * Creates an instance of the <code>CloudBlockBlob</code> class by copying values from another cloud block blob.
     * 
     * @param otherBlob
     *            A <code>CloudBlockBlob</code> object that represents the block blob to copy.
     * 
     * @throws StorageException
     *             If a storage service error occurs.
     */
    public CloudBlockBlob(final CloudBlockBlob otherBlob) throws StorageException {
        super(otherBlob);
    }

    /**
     * Creates an instance of the <code>CloudBlockBlob</code> class using the specified relative URI and storage service
     * client.
     * 
     * @param uri
     *            A <code>java.net.URI</code> object that represents the relative URI to the blob, beginning with the
     *            container name.
     * @param client
     *            A {@link CloudBlobClient} object that specifies the endpoint for the Blob service.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public CloudBlockBlob(final URI uri, final CloudBlobClient client) throws StorageException {
        this(new StorageUri(uri), client);
    }

    /**
     * Creates an instance of the <code>CloudBlockBlob</code> class using the specified relative URI and storage service
     * client.
     * 
     * @param uri
     *            A <code>StorageUri</code> object that represents the relative URI to the blob, beginning with the
     *            container name.
     * @param client
     *            A {@link CloudBlobClient} object that specifies the endpoint for the Blob service.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public CloudBlockBlob(final StorageUri uri, final CloudBlobClient client) throws StorageException {
        super(BlobType.BLOCK_BLOB, uri, client);
    }

    /**
     * Creates an instance of the <code>CloudBlockBlob</code> class using the specified relative URI, storage service
     * client and container.
     * 
     * @param uri
     *            A <code>java.net.URI</code> object that represents the relative URI to the blob.
     * @param client
     *            A {@link CloudBlobClient} object that specifies the endpoint for the Blob service.
     * @param container
     *            A {@link CloudBlobContainer} object that represents the container to use for the blob.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public CloudBlockBlob(final URI uri, final CloudBlobClient client, final CloudBlobContainer container)
            throws StorageException {
        this(new StorageUri(uri), client, container);
    }

    /**
     * Creates an instance of the <code>CloudBlockBlob</code> class using the specified relative URI, storage service
     * client and container.
     * 
     * @param uri
     *            A <code>StorageUri</code> object that represents the relative URI to the blob.
     * @param client
     *            A {@link CloudBlobClient} object that specifies the endpoint for the Blob service.
     * @param container
     *            A {@link CloudBlobContainer} object that represents the container to use for the blob.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public CloudBlockBlob(final StorageUri uri, final CloudBlobClient client, final CloudBlobContainer container)
            throws StorageException {
        super(BlobType.BLOCK_BLOB, uri, client, container);
    }

    /**
     * Creates an instance of the <code>CloudBlockBlob</code> class using the specified relative URI, snapshot ID, and
     * storage service client.
     * 
     * @param uri
     *            A <code>java.net.URI</code> object that represents the relative URI to the blob.
     * @param snapshotID
     *            A <code>String</code> that represents the snapshot version, if applicable.
     * @param client
     *            A {@link CloudBlobClient} object that specifies the endpoint for the Blob service.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public CloudBlockBlob(final URI uri, final String snapshotID, final CloudBlobClient client) throws StorageException {
        this(new StorageUri(uri), snapshotID, client);
    }

    /**
     * Creates an instance of the <code>CloudBlockBlob</code> class using the specified relative URI, snapshot ID, and
     * storage service client.
     * 
     * @param uri
     *            A <code>StorageUri</code> object that represents the relative URI to the blob.
     * @param snapshotID
     *            A <code>String</code> that represents the snapshot version, if applicable.
     * @param client
     *            A {@link CloudBlobClient} object that specifies the endpoint for the Blob service.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public CloudBlockBlob(final StorageUri uri, final String snapshotID, final CloudBlobClient client)
            throws StorageException {
        super(BlobType.BLOCK_BLOB, uri, snapshotID, client);
    }

    /**
     * Commits a block list to the storage service.
     * 
     * @param blockList
     *            An enumerable collection of <code>BlockEntry</code> objects that represents the list block items being
     *            committed. The <code>size</code> field is ignored.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    @DoesServiceRequest
    public void commitBlockList(final Iterable<BlockEntry> blockList) throws StorageException {
        this.commitBlockList(blockList, null /* accessCondition */, null /* options */, null /* opContext */);
    }

    /**
     * Commits a block list to the storage service using the specified lease ID, request options, and operation context.
     * 
     * @param blockList
     *            An enumerable collection of <code>BlockEntry</code> objects that represents the list block items being
     *            committed. The size field is ignored.
     * @param accessCondition
     *            An {@link AccessCondition} object that represents the access conditions for the blob.
     * @param options
     *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying
     *            <code>null</code> will use the default request options from the associated service client (
     *            {@link CloudBlobClient}).
     * @param opContext
     *            An {@link OperationContext} object that represents the context for the current operation. This object
     *            is used to track requests to the storage service, and to provide additional runtime information about
     *            the operation.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    @DoesServiceRequest
    public void commitBlockList(final Iterable<BlockEntry> blockList, final AccessCondition accessCondition,
            BlobRequestOptions options, OperationContext opContext) throws StorageException {
        assertNoWriteOperationForSnapshot();

        if (opContext == null) {
            opContext = new OperationContext();
        }

        options = BlobRequestOptions.applyDefaults(options, BlobType.BLOCK_BLOB, this.blobServiceClient);

        ExecutionEngine.executeWithRetry(this.blobServiceClient, this,
                this.commitBlockListImpl(blockList, accessCondition, options, opContext),
                options.getRetryPolicyFactory(), opContext);
    }

    private StorageRequest<CloudBlobClient, CloudBlob, Void> commitBlockListImpl(final Iterable<BlockEntry> blockList,
            final AccessCondition accessCondition, final BlobRequestOptions options, final OperationContext opContext)
            throws StorageException {

        byte[] blockListBytes;
        try {
            blockListBytes = BlockEntryListSerializer.writeBlockListToStream(blockList, opContext);

            final ByteArrayInputStream blockListInputStream = new ByteArrayInputStream(blockListBytes);

            // This also marks the stream. Therefore no need to mark it in buildRequest.
            final StreamMd5AndLength descriptor = Utility.analyzeStream(blockListInputStream, -1L, -1L,
                    true /* rewindSourceStream */, true /* calculateMD5 */);

            final StorageRequest<CloudBlobClient, CloudBlob, Void> putRequest = new StorageRequest<CloudBlobClient, CloudBlob, Void>(
                    options, this.getStorageUri()) {

                @Override
                public HttpURLConnection buildRequest(CloudBlobClient client, CloudBlob blob, OperationContext context)
                        throws Exception {
                    this.setSendStream(blockListInputStream);
                    this.setLength(descriptor.getLength());
                    return BlobRequest.putBlockList(
                            blob.getTransformedAddress(context).getUri(this.getCurrentLocation()),
                            options.getTimeoutIntervalInMs(), blob.properties, accessCondition, options, context);
                }

                @Override
                public void setHeaders(HttpURLConnection connection, CloudBlob blob, OperationContext context) {
                    BlobRequest.addMetadata(connection, blob.metadata, context);

                    connection.setRequestProperty(Constants.HeaderConstants.CONTENT_MD5, descriptor.getMd5());
                }

                @Override
                public void signRequest(HttpURLConnection connection, CloudBlobClient client, OperationContext context)
                        throws Exception {
                    StorageRequest.signBlobAndQueueRequest(connection, client, this.getLength(), null);
                }

                @Override
                public Void preProcessResponse(CloudBlob blob, CloudBlobClient client, OperationContext context)
                        throws Exception {
                    if (this.getResult().getStatusCode() != HttpURLConnection.HTTP_CREATED) {
                        this.setNonExceptionedRetryableFailure(true);
                        return null;
                    }

                    blob.updateEtagAndLastModifiedFromResponse(this.getConnection());
                    return null;
                }

                @Override
                public void recoveryAction(OperationContext context) throws IOException {
                    blockListInputStream.reset();
                    blockListInputStream.mark(Constants.MAX_MARK_LENGTH);
                }
            };

            return putRequest;
        }
        catch (XMLStreamException e) {
            // The request was not even made. There was an error while trying to write the block list. Just throw.
            StorageException translatedException = StorageException.translateException(null, e, null);
            throw translatedException;
        }
        catch (IOException e) {
            // The request was not even made. There was an error while trying to write the block list. Just throw.
            StorageException translatedException = StorageException.translateException(null, e, null);
            throw translatedException;
        }
    }

    /**
     * Downloads the committed block list from the block blob.
     * <p>
     * The committed block list includes the list of blocks that have been successfully committed to the block blob. The
     * list of committed blocks is returned in the same order that they were committed to the blob. No block may appear
     * more than once in the committed block list.
     * 
     * @return An <code>ArrayList</code> object of <code>BlockEntry</code> objects that represent the committed list
     *         block items downloaded from the block blob.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    @DoesServiceRequest
    public ArrayList<BlockEntry> downloadBlockList() throws StorageException {
        return this.downloadBlockList(BlockListingFilter.COMMITTED, null /* accessCondition */, null /* options */,
                null /* opContext */);
    }

    /**
     * Downloads the block list from the block blob using the specified block listing filter, request options, and
     * operation context.
     * 
     * @param blockListingFilter
     *            A {@link BlockListingFilter} value that specifies whether to download committed blocks, uncommitted
     *            blocks, or all blocks.
     * @param accessCondition
     *            An {@link AccessCondition} object that represents the access conditions for the blob.
     * @param options
     *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying
     *            <code>null</code> will use the default request options from the associated service client (
     *            {@link CloudBlobClient}).
     * @param opContext
     *            An {@link OperationContext} object that represents the context for the current operation. This object
     *            is used to track requests to the storage service, and to provide additional runtime information about
     *            the operation.
     * 
     * @return An <code>ArrayList</code> object of <code>BlockEntry</code> objects that represent the list block items
     *         downloaded from the block blob.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    @DoesServiceRequest
    public ArrayList<BlockEntry> downloadBlockList(final BlockListingFilter blockListingFilter,
            final AccessCondition accessCondition, BlobRequestOptions options, OperationContext opContext)
            throws StorageException {
        Utility.assertNotNull("blockListingFilter", blockListingFilter);

        if (opContext == null) {
            opContext = new OperationContext();
        }

        opContext.initialize();
        options = BlobRequestOptions.applyDefaults(options, BlobType.BLOCK_BLOB, this.blobServiceClient);

        return ExecutionEngine.executeWithRetry(this.blobServiceClient, this,
                this.downloadBlockListImpl(blockListingFilter, accessCondition, options),
                options.getRetryPolicyFactory(), opContext);
    }

    private StorageRequest<CloudBlobClient, CloudBlob, ArrayList<BlockEntry>> downloadBlockListImpl(
            final BlockListingFilter blockListingFilter, final AccessCondition accessCondition,
            final BlobRequestOptions options) throws StorageException {
        final StorageRequest<CloudBlobClient, CloudBlob, ArrayList<BlockEntry>> getRequest = new StorageRequest<CloudBlobClient, CloudBlob, ArrayList<BlockEntry>>(
                options, this.getStorageUri()) {

            @Override
            public void setRequestLocationMode() {
                this.setRequestLocationMode(RequestLocationMode.PRIMARY_OR_SECONDARY);
            }

            @Override
            public HttpURLConnection buildRequest(CloudBlobClient client, CloudBlob blob, OperationContext context)
                    throws Exception {
                return BlobRequest.getBlockList(blob.getTransformedAddress(context).getUri(this.getCurrentLocation()),
                        options.getTimeoutIntervalInMs(), blob.snapshotID, blockListingFilter, accessCondition,
                        options, context);
            }

            @Override
            public void signRequest(HttpURLConnection connection, CloudBlobClient client, OperationContext context)
                    throws Exception {
                StorageRequest.signBlobAndQueueRequest(connection, client, -1L, null);
            }

            @Override
            public ArrayList<BlockEntry> preProcessResponse(CloudBlob blob, CloudBlobClient client,
                    OperationContext context) throws Exception {
                if (this.getResult().getStatusCode() != HttpURLConnection.HTTP_OK) {
                    this.setNonExceptionedRetryableFailure(true);
                }

                return null;
            }

            @Override
            public ArrayList<BlockEntry> postProcessResponse(HttpURLConnection connection, CloudBlob blob,
                    CloudBlobClient client, OperationContext context, ArrayList<BlockEntry> storageObject)
                    throws Exception {
                blob.updateEtagAndLastModifiedFromResponse(this.getConnection());
                blob.updateLengthFromResponse(this.getConnection());

                return BlobDeserializer.getBlockList(this.getConnection().getInputStream());
            }
        };

        return getRequest;
    }

    /**
     * Creates and opens an output stream to write data to the block blob.
     * 
     * @return A {@link BlobOutputStream} object used to write data to the blob.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public BlobOutputStream openOutputStream() throws StorageException {
        return this.openOutputStream(null /* accessCondition */, null /* options */, null /* opContext */);
    }

    /**
     * Creates and opens an output stream to write data to the block blob using the specified request options and
     * operation context.
     * 
     * @param accessCondition
     *            An {@link AccessCondition} object that represents the access conditions for the blob.
     * @param options
     *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying
     *            <code>null</code> will use the default request options from the associated service client (
     *            {@link CloudBlobClient}).
     * @param opContext
     *            An {@link OperationContext} object that represents the context for the current operation. This object
     *            is used to track requests to the storage service, and to provide additional runtime information about
     *            the operation.
     * 
     * @return A {@link BlobOutputStream} object used to write data to the blob.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     */
    public BlobOutputStream openOutputStream(final AccessCondition accessCondition, BlobRequestOptions options,
            OperationContext opContext) throws StorageException {
        if (opContext == null) {
            opContext = new OperationContext();
        }

        assertNoWriteOperationForSnapshot();

        options = BlobRequestOptions.applyDefaults(options, BlobType.BLOCK_BLOB, this.blobServiceClient, false);

        return new BlobOutputStream(this, accessCondition, options, opContext);
    }

    /**
     * Uploads the source stream data to the block blob.
     * 
     * @param sourceStream
     *            An <code>InputStream</code> object that represents the input stream to write to the block blob.
     * @param length
     *            The length, in bytes, of the stream data, or -1 if unknown.
     * 
     * @throws IOException
     *             If an I/O error occurred.
     * @throws StorageException
     *             If a storage service error occurred.
     */
    @Override
    @DoesServiceRequest
    public void upload(final InputStream sourceStream, final long length) throws StorageException, IOException {
        this.upload(sourceStream, length, null /* accessCondition */, null /* options */, null /* opContext */);
    }

    /**
     * Uploads the source stream data to the blob, using the specified lease ID, request options, and operation context.
     * 
     * @param sourceStream
     *            An <code>InputStream</code> object that represents the input stream to write to the block blob.
     * @param length
     *            The length, in bytes, of the stream data, or -1 if unknown.
     * @param accessCondition
     *            An {@link AccessCondition} object that represents the access conditions for the blob.
     * @param options
     *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying
     *            <code>null</code> will use the default request options from the associated service client (
     *            {@link CloudBlobClient}).
     * @param opContext
     *            An {@link OperationContext} object that represents the context for the current operation. This object
     *            is used to track requests to the storage service, and to provide additional runtime information about
     *            the operation.
     * 
     * @throws IOException
     *             If an I/O error occurred.
     * @throws StorageException
     *             If a storage service error occurred.
     */
    @Override
    @DoesServiceRequest
    public void upload(final InputStream sourceStream, final long length, final AccessCondition accessCondition,
            BlobRequestOptions options, OperationContext opContext) throws StorageException, IOException {
        if (length < -1) {
            throw new IllegalArgumentException(SR.STREAM_LENGTH_NEGATIVE);
        }

        assertNoWriteOperationForSnapshot();

        if (opContext == null) {
            opContext = new OperationContext();
        }

        opContext.initialize();
        options = BlobRequestOptions.applyDefaults(options, BlobType.BLOCK_BLOB, this.blobServiceClient);

        StreamMd5AndLength descriptor = new StreamMd5AndLength();
        descriptor.setLength(length);

        if (sourceStream.markSupported()) {
            // Mark sourceStream for current position.
            sourceStream.mark(Constants.MAX_MARK_LENGTH);
        }

        // If the stream is rewindable and the length is unknown or we need to
        // set md5, then analyze the stream.
        // Note this read will abort at
        // options.getSingleBlobPutThresholdInBytes() bytes and return
        // -1 as length in which case we will revert to using a stream as it is
        // over the single put threshold.
        if (sourceStream.markSupported()
                && (length < 0 || (options.getStoreBlobContentMD5() && length <= options
                        .getSingleBlobPutThresholdInBytes()))) {
            // If the stream is of unknown length or we need to calculate
            // the MD5, then we we need to read the stream contents first

            descriptor = Utility.analyzeStream(sourceStream, length, options.getSingleBlobPutThresholdInBytes() + 1,
                    true /* rewindSourceStream */, options.getStoreBlobContentMD5());

            if (descriptor.getMd5() != null && options.getStoreBlobContentMD5()) {
                this.properties.setContentMD5(descriptor.getMd5());
            }
        }

        // If the stream is rewindable, and the length is known and less than
        // threshold the upload in a single put, otherwise use a stream.
        if (sourceStream.markSupported() && descriptor.getLength() != -1
                && descriptor.getLength() < options.getSingleBlobPutThresholdInBytes() + 1) {
            this.uploadFullBlob(sourceStream, descriptor.getLength(), accessCondition, options, opContext);
        }
        else {
            final BlobOutputStream writeStream = this.openOutputStream(accessCondition, options, opContext);
            try {
                writeStream.write(sourceStream, length);
            }
            finally {
                writeStream.close();
            }
        }
    }

    /**
     * Uploads a block to the block blob, using the specified block ID and lease ID.
     * 
     * @param blockId
     *            A <code>String</code> that represents the Base-64 encoded block ID. Note for a given blob the length
     *            of all Block IDs must be identical.
     * @param sourceStream
     *            An <code>InputStream</code> object that represents the input stream to write to the block blob.
     * @param length
     *            The length, in bytes, of the stream data, or -1 if unknown.
     * @throws IOException
     *             If an I/O error occurred.
     * @throws StorageException
     *             If a storage service error occurred.
     */
    @DoesServiceRequest
    public void uploadBlock(final String blockId, final InputStream sourceStream, final long length)
            throws StorageException, IOException {
        this.uploadBlock(blockId, sourceStream, length, null /* accessCondition */, null /* options */, null /* opContext */);
    }

    /**
     * Uploads a block to the block blob, using the specified block ID, lease ID, request options, and operation
     * context.
     * 
     * @param blockId
     *            A <code>String</code> that represents the Base-64 encoded block ID. Note for a given blob the length
     *            of all Block IDs must be identical.
     * 
     * @param sourceStream
     *            An <code>InputStream</code> object that represents the input stream to write to the block blob.
     * @param length
     *            The length, in bytes, of the stream data, or -1 if unknown.
     * @param accessCondition
     *            An {@link AccessCondition} object that represents the access conditions for the blob.
     * @param options
     *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying
     *            <code>null</code> will use the default request options from the associated service client (
     *            {@link CloudBlobClient}).
     * @param opContext
     *            An {@link OperationContext} object that represents the context for the current operation. This object
     *            is used to track requests to the storage service, and to provide additional runtime information about
     *            the operation.
     * 
     * @throws IOException
     *             If an I/O error occurred.
     * @throws StorageException
     *             If a storage service error occurred.
     */
    @DoesServiceRequest
    public void uploadBlock(final String blockId, final InputStream sourceStream, final long length,
            final AccessCondition accessCondition, BlobRequestOptions options, OperationContext opContext)
            throws StorageException, IOException {
        if (length < -1) {
            throw new IllegalArgumentException(SR.STREAM_LENGTH_NEGATIVE);
        }

        if (length > 4 * Constants.MB) {
            throw new IllegalArgumentException(SR.STREAM_LENGTH_GREATER_THAN_4MB);
        }

        assertNoWriteOperationForSnapshot();

        if (opContext == null) {
            opContext = new OperationContext();
        }

        options = BlobRequestOptions.applyDefaults(options, BlobType.BLOCK_BLOB, this.blobServiceClient);

        // Assert block length
        if (Utility.isNullOrEmpty(blockId) || !Base64.validateIsBase64String(blockId)) {
            throw new IllegalArgumentException(SR.INVALID_BLOCK_ID);
        }

        if (sourceStream.markSupported()) {
            // Mark sourceStream for current position.
            sourceStream.mark(Constants.MAX_MARK_LENGTH);
        }

        InputStream bufferedStreamReference = sourceStream;
        StreamMd5AndLength descriptor = new StreamMd5AndLength();
        descriptor.setLength(length);

        if (!sourceStream.markSupported()) {
            // needs buffering
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            descriptor = Utility.writeToOutputStream(sourceStream, byteStream, length, false /* rewindSourceStream */,
                    options.getUseTransactionalContentMD5(), opContext, options);

            bufferedStreamReference = new ByteArrayInputStream(byteStream.toByteArray());
        }
        else if (length < 0 || options.getUseTransactionalContentMD5()) {
            // If the stream is of unknown length or we need to calculate the
            // MD5, then we we need to read the stream contents first

            descriptor = Utility.analyzeStream(sourceStream, length, -1L, true /* rewindSourceStream */,
                    options.getUseTransactionalContentMD5());
        }

        if (descriptor.getLength() > 4 * Constants.MB) {
            throw new IllegalArgumentException(SR.STREAM_LENGTH_GREATER_THAN_4MB);
        }

        this.uploadBlockInternal(blockId, descriptor.getMd5(), bufferedStreamReference, descriptor.getLength(),
                accessCondition, options, opContext);
    }

    /**
     * Uploads a block of the blob to the server.
     * 
     * @param blockId
     *            the Base64 Encoded Block ID
     * @param md5
     *            the MD5 to use if it will be set.
     * @param sourceStream
     *            the InputStream to read from
     * @param length
     *            the OutputStream to write the blob to.
     * @param accessCondition
     *            An {@link AccessCondition} object that represents the access conditions for the blob.
     * @param options
     *            An object that specifies any additional options for the request
     * @param opContext
     *            an object used to track the execution of the operation
     * @throws StorageException
     *             If a storage service error occurred.
     * @throws IOException
     */
    @DoesServiceRequest
    private void uploadBlockInternal(final String blockId, final String md5, final InputStream sourceStream,
            final long length, final AccessCondition accessCondition, final BlobRequestOptions options,
            final OperationContext opContext) throws StorageException, IOException {
        ExecutionEngine.executeWithRetry(this.blobServiceClient, this,
                uploadBlockImpl(blockId, md5, sourceStream, length, accessCondition, options, opContext),
                options.getRetryPolicyFactory(), opContext);
    }

    private StorageRequest<CloudBlobClient, CloudBlob, Void> uploadBlockImpl(final String blockId, final String md5,
            final InputStream sourceStream, final long length, final AccessCondition accessCondition,
            final BlobRequestOptions options, final OperationContext opContext) throws StorageException, IOException {

        final StorageRequest<CloudBlobClient, CloudBlob, Void> putRequest = new StorageRequest<CloudBlobClient, CloudBlob, Void>(
                options, this.getStorageUri()) {

            @Override
            public HttpURLConnection buildRequest(CloudBlobClient client, CloudBlob blob, OperationContext context)
                    throws Exception {
                this.setSendStream(sourceStream);
                this.setLength(length);
                return BlobRequest.putBlock(blob.getTransformedAddress(opContext).getUri(this.getCurrentLocation()),
                        options.getTimeoutIntervalInMs(), blockId, accessCondition, options, opContext);
            }

            @Override
            public void setHeaders(HttpURLConnection connection, CloudBlob blob, OperationContext context) {
                if (options.getUseTransactionalContentMD5()) {
                    connection.setRequestProperty(Constants.HeaderConstants.CONTENT_MD5, md5);
                }
            }

            @Override
            public void signRequest(HttpURLConnection connection, CloudBlobClient client, OperationContext context)
                    throws Exception {
                StorageRequest.signBlobAndQueueRequest(connection, client, length, null);
            }

            @Override
            public Void preProcessResponse(CloudBlob blob, CloudBlobClient client, OperationContext context)
                    throws Exception {
                if (this.getResult().getStatusCode() != HttpURLConnection.HTTP_CREATED) {
                    this.setNonExceptionedRetryableFailure(true);
                    return null;
                }

                return null;
            }

            @Override
            public void recoveryAction(OperationContext context) throws IOException {
                sourceStream.reset();
                sourceStream.mark(Constants.MAX_MARK_LENGTH);
            }
        };

        return putRequest;
    }

    /**
     * Uploads a blob from a string using the platform's default encoding.
     * 
     * @param content
     *            A string, the content of which will be uploaded to the blob.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     * @throws IOException
     */
    public void uploadText(final String content) throws StorageException, IOException {
        this.uploadText(content, null /* charsetName */, null /* accessCondition */, null /* options */, null /* opContext */);
    }

    /**
     * Uploads a blob from a string using the specified encoding.
     * 
     * @param content
     *            A string, the content of which will be uploaded to the blob.
     * @param charsetName
     *            The name of the charset to use to encode the content. If null, the platform's default encoding is
     *            used.
     * @param accessCondition
     *            An {@link AccessCondition} object that represents the access conditions for the blob.
     * @param options
     *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying
     *            <code>null</code> will use the default request options from the associated service client (
     *            {@link CloudBlobClient}).
     * @param opContext
     *            An {@link OperationContext} object that represents the context for the current operation. This object
     *            is used to track requests to the storage service, and to provide additional runtime information about
     *            the operation.
     * 
     * @throws StorageException
     *             If a storage service error occurred.
     * @throws IOException
     */
    public void uploadText(final String content, final String charsetName, final AccessCondition accessCondition,
            BlobRequestOptions options, OperationContext opContext) throws StorageException, IOException {
        byte[] bytes = (charsetName == null) ? content.getBytes() : content.getBytes(charsetName);
        this.uploadFromByteArray(bytes, 0, bytes.length, accessCondition, options, opContext);
    }

    /**
     * Downloads a blob to a string using the platform's default encoding.
     * 
     * @return
     *         String representation of the blob's contents.
     * @throws StorageException
     *             If a storage service error occurred.
     * @throws IOException
     */
    public String downloadText() throws StorageException, IOException {
        return this
                .downloadText(null /* charsetName */, null /* accessCondition */, null /* options */, null /* opContext */);
    }

    /**
     * Downloads a blob to a string using the specified encoding.
     * 
     * @param charsetName
     *            The name of the charset to use to decode the blob into a string. If null, the platform's default
     *            encoding is used.
     *            encoding.
     * @param accessCondition
     *            An {@link AccessCondition} object that represents the access conditions for the blob.
     * @param options
     *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying
     *            <code>null</code> will use the default request options from the associated service client (
     *            {@link CloudBlobClient}).
     * @param opContext
     *            An {@link OperationContext} object that represents the context for the current operation. This object
     *            is used to track requests to the storage service, and to provide additional runtime information about
     *            the operation.
     * 
     * @return
     *         String representation of the blob's contents.
     * @throws StorageException
     *             If a storage service error occurred.
     * @throws IOException
     */
    public String downloadText(final String charsetName, final AccessCondition accessCondition,
            BlobRequestOptions options, OperationContext opContext) throws StorageException, IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.download(baos, accessCondition, options, opContext);
        return charsetName == null ? baos.toString() : baos.toString(charsetName);
    }

    /**
     * Sets the number of bytes to buffer when writing to a {@link BlobOutputStream}.
     * 
     * @param writeBlockSizeInBytes
     *            The maximum block size, in bytes, for writing to a block blob while using a {@link BlobOutputStream}
     *            object, ranging from 16 KB to 4 MB, inclusive.
     * 
     * @throws IllegalArgumentException
     *             If <code>streamWriteSizeInBytes</code> is less than 16 KB or greater than 4 MB.
     */
    @Override
    public void setStreamWriteSizeInBytes(final int streamWriteSizeInBytes) {
        if (streamWriteSizeInBytes > BlobConstants.MAX_COMMIT_SIZE_4_MB || streamWriteSizeInBytes < 16 * Constants.KB) {
            throw new IllegalArgumentException("StreamWriteSizeInBytes");
        }

        this.streamWriteSizeInBytes = streamWriteSizeInBytes;
    }
}
