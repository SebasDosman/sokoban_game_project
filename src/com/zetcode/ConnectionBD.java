package com.zetcode;

import java.sql.*;

public class ConnectionBD {
  Connection conexion;

  public String createUser(String name_player, int points_player) {
    try {
      conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Sokoban", "postgres", "123");
      Statement statement = conexion.createStatement();
      ResultSet resultado = statement.executeQuery("SELECT name_player, points_player " +
          "FROM public.points " +
          "WHERE name_player='" + name_player + "';");
      if (resultado.next()) {
        int sumBD = resultado.getInt("points_player");
        int sum = sumBD + points_player;
        statement.executeUpdate("UPDATE public.points " +
            "SET points_player="+sum+"WHERE name_player='" + name_player + "';");
      } else {
        statement.executeUpdate("INSERT INTO public.points (name_player, points_player) " +
            "VALUES ('" + name_player + "', " + points_player + ");");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "Enviado";
  }
}
