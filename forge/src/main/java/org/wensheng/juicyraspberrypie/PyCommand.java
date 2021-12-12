package org.wensheng.juicyraspberrypie;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PyCommand {
	private static Logger logger = JuicyRaspberryPieMod.LOGGER;

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("p").then(
						Commands.argument("arg", MessageArgument.message())
								.executes(PyCommand::executeCommand)
				)
		);
	}

	private static int executeCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		String replyString = "";
		logger.info("Execute command ...");
		// TODO: this is probably wrong
		TextComponent arg = (TextComponent) MessageArgument.getMessage(ctx, "arg");
		//ServerPlayerEntity player = ctx.getSource().asPlayer();

		try {
			String addr = JRPModConfig.SERVER.pysvr_addr.get().toString();
			int port = JRPModConfig.SERVER.pysvr_port.get();
			logger.info("connecting to " + addr + ":" + port);
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(addr, port), 1000);
			DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream());
			BufferedReader fromPyServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			toPyServer.writeUTF(arg.getString());
			replyString = fromPyServer.readLine();
			//LOGGER.info("the py server send back " + replyString);
			toPyServer.close();
			fromPyServer.close();
			socket.close();
		} catch (Exception e) {
			TextComponent message = new TextComponent("No Python Command Server available");
			ctx.getSource().sendFailure(message);
			logger.error("No JRP command server available.");
		}

		if(!replyString.equals("ok")){
			TextComponent message = new TextComponent(replyString);
			ctx.getSource().sendSuccess(message, true);
		}

		return 1;
	}

}
