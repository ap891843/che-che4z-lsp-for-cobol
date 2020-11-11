/*
 * Copyright (c) 2020 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Broadcom, Inc. - initial API and implementation
 *
 */
package com.broadcom.lsp.cobol;

import com.broadcom.lsp.cobol.domain.modules.DatabusModule;
import com.broadcom.lsp.cobol.domain.modules.EngineModule;
import com.broadcom.lsp.cobol.domain.modules.ServiceModule;
import com.broadcom.lsp.cobol.service.providers.ClientProvider;
import com.broadcom.lsp.cobol.service.utils.CustomThreadPoolExecutor;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * This class is an entry point for the application. It initializes the DI context and runs the
 * server to accept the connections using either socket on LSP_PORT or pipes using STDIO. After the
 * establishing of the connection the main thread suspends until it is stopped forcibly.
 *
 * <p>To run the extension using path, you may specify "pipeEnabled" as a program argument. In other
 * case the server will start using socket.
 */
@Slf4j
@UtilityClass
public class LangServerBootstrap {
  private static final Integer LSP_PORT = 1044;
  private static final String PIPE_ARG = "pipeEnabled";

  public static void main(String[] args)
      throws ExecutionException, InterruptedException, IOException {
    Injector injector = initCtx();
    LanguageServer server = injector.getInstance(LanguageServer.class);
    ClientProvider provider = injector.getInstance(ClientProvider.class);
    CustomThreadPoolExecutor customExecutor = injector.getInstance(CustomThreadPoolExecutor.class);

    start(args, server, provider, customExecutor.getThreadPoolExecutor());
  }

  Injector initCtx() {
    return Guice.createInjector(new ServiceModule(), new EngineModule(), new DatabusModule());
  }

  private void start(
      @NonNull String[] args,
      @NonNull LanguageServer server,
      @NonNull ClientProvider provider,
      @NonNull ExecutorService executorService)
      throws IOException, InterruptedException, ExecutionException {
    try {
      Launcher<LanguageClient> launcher = launchServer(args, server, executorService);
      provider.set(launcher.getRemoteProxy());
      // suspend the main thread on listening
      launcher.startListening().get();
    } catch (ExecutionException e) {
      LOG.error("An error occurred while starting a language server", e);
      throw e;
    } catch (IOException e) {
      LOG.error("Unable to start server using socket communication on port [{}]", LSP_PORT);
      throw e;
    } finally {
      executorService.shutdown();
    }
  }

  Launcher<LanguageClient> launchServer(
      @NonNull String[] args,
      @NonNull LanguageServer server,
      @NonNull ExecutorService executorService)
      throws IOException {
    return isPipeEnabled(args)
        ? createServerLauncher(server, System.in, System.out, executorService)
        : createServerLauncherWithSocket(server, executorService);
  }

  boolean isPipeEnabled(@NonNull String[] args) {
    return args.length > 0 && PIPE_ARG.equals(args[0]);
  }

  Launcher<LanguageClient> createServerLauncherWithSocket(
      @NonNull LanguageServer server, @NonNull ExecutorService executorService) throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(LSP_PORT)) {
      LOG.info("Language server started using socket communication on port [{}]", LSP_PORT);
      LOG.info("Java version: " + Runtime.version());
      // wait for clients to connect
      Socket socket = serverSocket.accept();
      return createServerLauncher(
          server, socket.getInputStream(), socket.getOutputStream(), executorService);
    }
  }

  Launcher<LanguageClient> createServerLauncher(
      @NonNull LanguageServer server,
      @NonNull InputStream in,
      @NonNull OutputStream out,
      @NonNull ExecutorService executorService) {
    return LSPLauncher.createServerLauncher(server, in, out, executorService, null);
  }
}
