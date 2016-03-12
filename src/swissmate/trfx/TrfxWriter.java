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

package swissmate.trfx;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import swtlib.tournament.Pairing;
import swtlib.tournament.Player;
import swtlib.tournament.SingleResult;
import swtlib.tournament.Tournament;

public class TrfxWriter {

	public static void write(File trfxFile, Tournament tournament, int untilRound) throws IOException {
	
		RandomAccessFile trfxRaFile = null;
		
		try {
			
			// if the TRFX file already exists, delete it
			trfxFile.delete();
			
			// open the TRFX file
			trfxRaFile = new RandomAccessFile(trfxFile, "rw");
			
			// write number of rounds
			trfxRaFile.writeBytes(String.format("XXR %d\r\n", tournament.getRounds().size()));
			
			// write the player line for each player
			for(Player player : tournament.getPlayers()) {
				if(!player.isBye()) {
					trfxRaFile.writeBytes(getPlayerLine(player, tournament, untilRound));
				}
			}
		
		}
		catch(IllegalStateException e) {
			// simply pass the exception to the caller
			throw e;
		}
		catch(IOException e) {
			// simply pass the exception to the caller
			throw e;
		}
		finally {
			// close the TRFX file
			if(trfxRaFile != null) { 
				try {
					trfxRaFile.close();
				}
				catch(IOException e) {}
			}
		}
		
	}
	
	private static String getPlayerLine(Player player, Tournament tournament, int untilRound) {
		
		// each player line begins with a constant id
		String playerLine = Constants.playerLineDataId;
		
		// go to the start rank position
		for(int i = playerLine.length(); i < Constants.playerLineStartRankOffset; i++) {
			playerLine += ' ';
		}
		
		// write the start rank
		playerLine += String.format("%4d", player.getStartRank());
		
		// go to the name position
		for(int i = playerLine.length(); i < Constants.playerLineNameOffset; i++) {
			playerLine += ' ';
		}
		
		// write the name
		playerLine += player.getName();
		
		String playerLinePairings = "";
		int pointsDoubled = 0;
		
		for(int roundIndex = 0; roundIndex <= untilRound; roundIndex++) {
			
			playerLinePairings += "  ";
			
			Pairing pairing = null;
			
			try {
				pairing = player.getPairings().get(roundIndex);
			}
			catch (IndexOutOfBoundsException e) {
				throw new IllegalStateException(String.format("Player %s has no pairing entry for round %d.", player.getName(), roundIndex + 1));
			}
			
			if(pairing == null) {
				// TRF extension for an unpaired player
				playerLinePairings += "0000 - -";
				continue;
			}
			
			boolean white = (player == pairing.getWhitePlayer());
			
			// check for consistency
			if(!white && player != pairing.getBlackPlayer()) {
				throw new IllegalStateException(String.format("Pairing of %s in round %d invalid.", player.getName(), roundIndex + 1));
			}
			
			Player opponent = white ? pairing.getBlackPlayer() : pairing.getWhitePlayer();
			
			if(opponent.isBye()) {
				// according to TRF extension, add "0000" as opponent start rank and "-" as color
				playerLinePairings += "0000 - ";
			}
			else {
				// add opponent start rank number and color
				playerLinePairings += String.format("%4d", opponent.getStartRank());
				playerLinePairings += white ? " w " : " b ";
			}
			
			// add the result
			SingleResult singleResult = white ? pairing.getResult().getWhiteResult() : pairing.getResult().getBlackResult();
			playerLinePairings += singleResultToPlayerLineSybmol(singleResult, pairing.getResult().isByDefault());
			
			// keep an account of the player's total points
			pointsDoubled += singleResult.getPointsDoubled();
			
		}
		
		// if the player is deactivated, add an additional entry if there is at least one round left to pair
		if(!player.isActive() && untilRound < tournament.getRounds().size()) {
			playerLinePairings += "  0000 - -";
		}
		
		// go to the points position
		for(int i = playerLine.length(); i < Constants.playerLinePointsOffset; i++) {
			playerLine += ' ';
		}
		
		// add the points as calculated above
		playerLine += pointsDoubledToPoints(pointsDoubled);
		
		// go to the pairings position
		for(int i = playerLine.length(); i < Constants.playerLinePairingsOffset; i++) {
			playerLine += ' ';
		}
		
		// add the pairings string
		playerLine += playerLinePairings;
		
		// end the line
		playerLine += "\r\n";
		
		return playerLine;
		
	}
	
	private static String singleResultToPlayerLineSybmol(SingleResult singleResult, boolean byDefault) {
		
		switch(singleResult) {
		case NONE:
			return " ";
		case LOSS:
			return byDefault? "-" : "0";
		case DRAW:
			return "=";
		case WIN:
			return byDefault ? "+" : "1";
		default:
			return " ";
		}
		
	}
	
	private static String pointsDoubledToPoints(int pointsDoubled) {
		
		String points = String.format("%2d", pointsDoubled / 2);
		points += (pointsDoubled % 2 == 1) ? ".5" : ".0";
		return points;
		
	}
	
}
