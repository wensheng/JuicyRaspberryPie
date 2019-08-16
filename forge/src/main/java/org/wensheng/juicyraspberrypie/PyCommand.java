package org.wensheng.juicyraspberrypie;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PyCommand {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, DyeColor> dyeColors = Arrays.stream(DyeColor.values()).collect(Collectors.toMap(
			d -> d.getName().replace("_", ""), Function.identity()));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("py").then(
						Commands.argument("arg", MessageArgument.message())
								.executes(PyCommand::executeCommand)
				)
		);
	}

	private static int executeCommand(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		String replyString = "";
		LOGGER.info("Execute command ...");
		ITextComponent arg = MessageArgument.getMessage(ctx, "arg");
		//ServerPlayerEntity player = ctx.getSource().asPlayer();

		try {
			String addr = JRPModConfig.SERVER.pysvr_addr.get().toString();
			int port = JRPModConfig.SERVER.pysvr_port.get();
			LOGGER.info("connecting to " + addr + ":" + port);
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(addr, port), 1000);
			DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream());
			BufferedReader fromPyServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			toPyServer.writeUTF(arg.getString());
			replyString = fromPyServer.readLine();
			//LOGGER.info("the py server send back " + replyString);
			toPyServer.close();
			fromPyServer.close();
			socket.close();
		} catch (Exception e) {
			ITextComponent message = new StringTextComponent("No Python Command Server available");
			ctx.getSource().sendFeedback(message, true);
			LOGGER.error("No python command server available.");
		}

		if(!replyString.equals("ok")){
			ITextComponent message = new StringTextComponent(replyString);
			ctx.getSource().sendFeedback(message, true);
		}

		return 1;
	}

}
