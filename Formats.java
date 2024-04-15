package kaba4cow.example;

import java.io.IOException;
import kaba4cow.bfdreader.binary.BinaryReader;

public class Formats {

	public static class PropertyData {

		public final String name;
		public final String value;

		private PropertyData(BinaryReader reader) throws IOException {
			this.name = reader.readString();
			this.value = reader.readString();
		}

	}

	public static class PackageData {

		public final long entryCount;
		public final EntryData[] entries;

		public PackageData(BinaryReader reader) throws IOException {
			this.entryCount = reader.readUnsignedInt();
			this.entries = new EntryData[(int) (entryCount)];
			for (int entries_index_generated = 0; entries_index_generated < this.entries.length; entries_index_generated++)
				this.entries[entries_index_generated] = new EntryData(reader);
		}

		public static class EntryData {

			public final String name;
			public final short type;
			public final long size;
			public final short[] data;

			private EntryData(BinaryReader reader) throws IOException {
				this.name = reader.readString();
				this.type = reader.readUnsignedByte();
				this.size = reader.readUnsignedInt();
				this.data = new short[(int) (size)];
				for (int data_index_generated = 0; data_index_generated < this.data.length; data_index_generated++)
					this.data[data_index_generated] = reader.readUnsignedByte();
			}

		}

	}

	public static class GraphData {

		public final long pointCount;
		public final PointData[] points;

		public GraphData(BinaryReader reader) throws IOException {
			this.pointCount = reader.readUnsignedInt();
			this.points = new PointData[(int) (pointCount)];
			for (int points_index_generated = 0; points_index_generated < this.points.length; points_index_generated++)
				this.points[points_index_generated] = new PointData(reader);
		}

		public static class PointData {

			public final double x;
			public final float y;

			private PointData(BinaryReader reader) throws IOException {
				this.x = reader.readDouble();
				this.y = reader.readFloat2();
			}

		}

	}

	public static class ModelData {

		public final int positionCount;
		public final float[] position;
		public final int textureCount;
		public final float[] textures;
		public final short hasColors;
		public final int colorCount;
		public final ColorData[] colors;
		public final int vertexCount;
		public final VertexData[] vertices;

		public ModelData(BinaryReader reader) throws IOException {
			this.positionCount = reader.readUnsignedShort();
			this.position = new float[(int) (positionCount)];
			for (int position_index_generated = 0; position_index_generated < this.position.length; position_index_generated++)
				this.position[position_index_generated] = reader.readFloat2();
			this.textureCount = reader.readUnsignedShort();
			this.textures = new float[(int) (textureCount)];
			for (int textures_index_generated = 0; textures_index_generated < this.textures.length; textures_index_generated++)
				this.textures[textures_index_generated] = reader.readFloat2();
			this.hasColors = reader.readUnsignedByte();
			if (hasColors != 0) {
				this.colorCount = reader.readUnsignedShort();
				this.colors = new ColorData[(int) (colorCount)];
				for (int colors_index_generated = 0; colors_index_generated < this.colors.length; colors_index_generated++)
					this.colors[colors_index_generated] = new ColorData(reader);
			} else {
				this.colorCount = (int) 0;
				this.colors = new ColorData[(int) (colorCount)];
			}
			this.vertexCount = reader.readUnsignedShort();
			this.vertices = new VertexData[(int) (vertexCount)];
			for (int vertices_index_generated = 0; vertices_index_generated < this.vertices.length; vertices_index_generated++)
				this.vertices[vertices_index_generated] = new VertexData(reader);
		}

		public static class VertexData {

			public final int position;
			public final int texture;
			public final short normal;
			public final int color;

			private VertexData(BinaryReader reader) throws IOException {
				this.position = reader.readUnsignedShort();
				this.texture = reader.readUnsignedShort();
				this.normal = reader.readUnsignedByte();
				this.color = reader.readUnsignedShort();
			}

		}

		public static class ColorData {

			public final short red;
			public final short green;
			public final short blue;

			private ColorData(BinaryReader reader) throws IOException {
				this.red = reader.readUnsignedByte();
				this.green = reader.readUnsignedByte();
				this.blue = reader.readUnsignedByte();
			}

		}

	}

}
