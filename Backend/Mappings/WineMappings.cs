using VinheriaAgnelloCRUD.Data.Models;
using VinheriaAgnelloCRUD.DTOs.Wines;

namespace VinheriaAgnelloCRUD.Mappings;

public static class WineMappings
{
    public static Wine ToEntity(this CreateWineRequest request)
    {
        return new Wine
        {
            Name = request.Name,
            Winery = request.Winery,
            Type = request.Type,
            Country = request.Country,
            Year = request.Year,
            Quantity = request.Quantity,
            Price = request.Price
        };
    }

    public static WineResponse ToResponse(this Wine wine)
    {
        return new WineResponse
        {
            Id = wine.Id,
            Name = wine.Name,
            Winery = wine.Winery,
            Type = wine.Type,
            Country = wine.Country,
            Year = wine.Year,
            Quantity = wine.Quantity,
            Price = wine.Price,
            CreatedAt = wine.CreatedAt
        };
    }

    public static void ApplyUpdate(this Wine wine, UpdateWineRequest request)
    {
        wine.Name = request.Name;
        wine.Winery = request.Winery;
        wine.Type = request.Type;
        wine.Country = request.Country;
        wine.Year = request.Year;
        wine.Quantity = request.Quantity;
        wine.Price = request.Price;
    }
}
