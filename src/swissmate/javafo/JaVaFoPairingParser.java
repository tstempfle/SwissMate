/*
 * Copyright 2015 Tobias Stempfle <tobias.stempfle@gmx.net>
 * 
 * This file is part of SwissMate.
 * 
 * SwissMate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SwissMate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SwissMate.  If not, see <http://www.gnu.org/licenses/>.
 */

package swissmate.javafo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import swtlib.tournament.Pairing;
import swtlib.tournament.PairingResult;
import swtlib.tournament.Player;
import swtlib.tournament.SingleResult;
import swtlib.tournament.Tournament;

public class JaVaFoPairingParser {

	public static ArrayList<Pairing> parse(File javafoPairings, Tournament tournament) throws IOException {
		
		RandomAccessFile javaoPairingsRaFile = null;
		
		try {
			
			// open the JaVaFo pairings file
			javaoPairingsRaFile = new RandomAccessFile(javafoPairings, "r");
			
			// determine number of pairings
			String line = javaoPairingsRaFile.readLine();
			Integer numPairings;
			try {
				numPairings = new Integer(line);
			}
			catch(NumberFormatException e) {
				throw new IllegalStateException("Unexpected number of pairings line:\n" + line);
			}
			
			ArrayList<Pairing> pairings = new ArrayList<Pairing>();
			boolean byePlayerPaired = false;
			
			for(int pairingIndex = 0; pairingIndex < numPairings; pairingIndex++) {
				
				PairingResult result = new PairingResult();
				
				line = javaoPairingsRaFile.readLine();
				
				String[] playerIndices = line.split(" ");
				if(playerIndices.length != 2) {
					throw new IllegalStateException("Unexpected pairing line:\n" + line);
				}
				
				Integer whitePlayerIndex;
				Integer blackPlayerIndex;
				try {
					whitePlayerIndex = new Integer(playerIndices[0]) - 1;
					blackPlayerIndex = new Integer(playerIndices[1]) - 1;
				}	
				catch(NumberFormatException e) {
					throw new IllegalStateException("Unexpected pairing line:\n" + line);
				}
				
				if(whitePlayerIndex == -1) {
					whitePlayerIndex = getByePlayer(tournament.getPlayers(), tournament.getRounds().size(), true);
					byePlayerPaired = true;
					result.setWhiteResult(SingleResult.LOSS);
					result.setBlackResult(SingleResult.WIN);
					result.setByDefault(true);
				}
				if(blackPlayerIndex == -1) {
					blackPlayerIndex = getByePlayer(tournament.getPlayers(), tournament.getRounds().size(), true);
					byePlayerPaired = true;
					result.setWhiteResult(SingleResult.WIN);
					result.setBlackResult(SingleResult.LOSS);
					result.setByDefault(true);
				}
				
				Player whitePlayer;
				Player blackPlayer;
				try {
					whitePlayer = tournament.getPlayers().get(whitePlayerIndex);
					blackPlayer = tournament.getPlayers().get(blackPlayerIndex);
				}
				catch(ArrayIndexOutOfBoundsException e) {
					throw new IllegalStateException("Player index out of range:\n" + line);
				}
				
				pairings.add(new Pairing(whitePlayer, blackPlayer, pairingIndex + 1, result));
				
			}
			
			// activate or deactivate the bye player if there is one
			int byePlayerIndex = getByePlayer(tournament.getPlayers(), tournament.getRounds().size(), false);
			if(byePlayerIndex >= 0) {
				tournament.getPlayers().get(byePlayerIndex).setActive(byePlayerPaired);
			}
			
			return pairings;
			
		}
		catch(IOException e) {
			// simply pass the exception to the caller
			throw e;
		}
		finally {
			// close the JaVaFo pairings file
			if(javaoPairingsRaFile != null) { 
				try {
					javaoPairingsRaFile.close();
				}
				catch(IOException e) {}
			}
		}
		
	}
	
	private static int getByePlayer(ArrayList<Player> players, int numRounds, boolean createNewByePlayer) {
		
		// check if there already is a bye player
		for(int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
			if(players.get(playerIndex).isBye()) {
				return playerIndex;
			}
		}
		
		if(createNewByePlayer) {
			// create a new bye player
			players.add(new Player(players.size() + 1, "spielfrei", "", "", "", "", true, true, numRounds));
			return players.size() - 1;
		}
		
		return -1;
		
	}
	
}
