package com.appleframework.flume.file.channel;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.annotations.Disposable;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;

import com.appleframework.flume.ng.configuration.utils.ContextUtil;

/**
 * <p>
 * A durable {@link Channel} implementation that uses the local file system for
 * its storage.
 * </p>
 * <p>
 * FileChannel works by writing all transactions to a set of directories
 * specified in the configuration. Additionally, when a commit occurs
 * the transaction is synced to disk.
 * </p>
 * <p>
 * FileChannel is marked
 * {@link org.apache.flume.annotations.InterfaceAudience.Private} because it
 * should only be instantiated via a configuration. For example, users should
 * certainly use FileChannel but not by instantiating FileChannel objects.
 * Meaning the label Private applies to user-developers not user-operators.
 * In cases where a Channel is required by instantiated by user-developers
 * {@link org.apache.flume.channel.MemoryChannel} should be used.
 * </p>
 */
@InterfaceAudience.Private
@InterfaceStability.Stable
@Disposable
public class FileChannel extends org.apache.flume.channel.file.FileChannel {

	@Override
	public void configure(Context context) {
		ContextUtil.fullContextValue(context);
		super.configure(context);
	}
	
}

